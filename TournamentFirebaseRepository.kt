package com.appsdevs.popit

import com.google. firebase.auth.FirebaseAuth
import com.google.firebase. firestore. FieldValue
import com.google.firebase. firestore.FirebaseFirestore
import com.google.firebase. firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow. callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.max

/**
 * Repositorio para manejar el leaderboard del torneo en Firebase Firestore
 */
class TournamentFirebaseRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth. getInstance()

    companion object {
        private const val COLLECTION_TOURNAMENTS = "tournaments"
        private const val COLLECTION_LEADERBOARD = "leaderboard"
        private const val COLLECTION_CONFIG = "config"
        private const val DOC_CURRENT = "current"
        private const val DOC_TOURNAMENT_INFO = "tournament_info"

        // Límite de jugadores en el leaderboard
        private const val LEADERBOARD_LIMIT = 200L
    }

    // ==================== AUTENTICACIÓN ANÓNIMA ====================

    /**
     * Asegura que el usuario tenga una sesión anónima en Firebase
     * Retorna el UID de Firebase
     */
    suspend fun ensureAnonymousAuth(): String {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return currentUser.uid
        }

        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: throw Exception("Failed to sign in anonymously")
    }

    /**
     * Obtiene el UID actual de Firebase (null si no está autenticado)
     */
    fun getCurrentFirebaseUid(): String? {
        return auth.currentUser?. uid
    }

    // ==================== TOURNAMENT INFO ====================

    /**
     * Obtiene la información del torneo actual (epoch, estado, etc.)
     */
    suspend fun getTournamentInfo(): TournamentInfo?  {
        return try {
            val doc = firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_CONFIG)
                .document(DOC_TOURNAMENT_INFO)
                .get()
                .await()

            if (doc. exists()) {
                TournamentInfo(
                    epochMillis = doc. getLong("epochMillis") ?: 0L,
                    tournamentId = doc. getString("tournamentId") ?: "",
                    createdAt = doc. getLong("createdAt") ?: 0L
                )
            } else {
                null
            }
        } catch (e:  Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Crea o actualiza la información del torneo
     * Solo debería llamarse cuando se inicia un nuevo torneo
     */
    suspend fun createOrUpdateTournamentInfo(epochMillis: Long): Boolean {
        return try {
            val tournamentId = "tournament_$epochMillis"
            val data = hashMapOf(
                "epochMillis" to epochMillis,
                "tournamentId" to tournamentId,
                "createdAt" to System.currentTimeMillis()
            )

            firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_CONFIG)
                . document(DOC_TOURNAMENT_INFO)
                .set(data)
                .await()

            true
        } catch (e:  Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Reinicia el torneo - limpia el leaderboard y crea nuevo epoch
     */
    suspend fun resetTournament(newEpochMillis: Long): Boolean {
        return try {
            // 1. Actualizar info del torneo
            createOrUpdateTournamentInfo(newEpochMillis)

            // 2. Eliminar todos los documentos del leaderboard actual
            val leaderboardRef = firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_LEADERBOARD)

            val docs = leaderboardRef.get().await()

            // Usar batches para eliminar eficientemente (máximo 500 por batch)
            val batchSize = 500
            val documents = docs.documents

            for (i in documents.indices step batchSize) {
                val batch = firestore.batch()
                val end = minOf(i + batchSize, documents. size)
                for (j in i until end) {
                    batch.delete(documents[j]. reference)
                }
                batch.commit().await()
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ==================== LEADERBOARD ====================

    /**
     * Envía o actualiza el score de un jugador en el leaderboard
     */
    suspend fun submitScore(
        oduserId: String,
        firebaseUid: String,
        name: String,
        avatarRes: Int,
        score: Int,
        generatedAvatarId: Int,
        bannerColorId: Int,
        highScore: Int,
        totalPops: Int,
        bestClickPercent: Int,
        challengesCompleted: Int,
        level: Int,
        bestStreak: Int,
        maxConsecutiveDays: Int
    ): Boolean {
        return try {
            val docRef = firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_LEADERBOARD)
                .document(oduserId)

            // Primero verificamos si ya existe para obtener el mejor score
            val existingDoc = docRef.get().await()
            val existingScore = if (existingDoc. exists()) {
                existingDoc.getLong("score")?.toInt() ?: 0
            } else {
                0
            }

            val bestScore = max(existingScore, score)

            val data = hashMapOf(
                "oduserId" to oduserId,
                "firebaseUid" to firebaseUid,
                "name" to name,
                "avatarRes" to avatarRes,
                "score" to bestScore,
                "generatedAvatarId" to generatedAvatarId,
                "bannerColorId" to bannerColorId,
                "highScore" to highScore,
                "totalPops" to totalPops,
                "bestClickPercent" to bestClickPercent,
                "challengesCompleted" to challengesCompleted,
                "level" to level,
                "bestStreak" to bestStreak,
                "maxConsecutiveDays" to maxConsecutiveDays,
                "updatedAt" to System.currentTimeMillis()
            )

            docRef. set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Actualiza solo el perfil del usuario (sin cambiar el score)
     * MEJORADO: Ahora crea el documento si no existe
     */
    suspend fun updatePlayerProfile(
        oduserId: String,
        name: String,
        avatarRes: Int,
        generatedAvatarId: Int,
        bannerColorId:  Int,
        highScore: Int,
        totalPops:  Int,
        bestClickPercent: Int,
        challengesCompleted: Int,
        level:  Int,
        bestStreak: Int,
        maxConsecutiveDays: Int
    ): Boolean {
        return try {
            val docRef = firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_LEADERBOARD)
                .document(oduserId)

            val existingDoc = docRef.get().await()

            if (existingDoc. exists()) {
                // Documento existe, solo actualizar campos de perfil
                val updates = hashMapOf<String, Any>(
                    "name" to name,
                    "avatarRes" to avatarRes,
                    "generatedAvatarId" to generatedAvatarId,
                    "bannerColorId" to bannerColorId,
                    "highScore" to highScore,
                    "totalPops" to totalPops,
                    "bestClickPercent" to bestClickPercent,
                    "challengesCompleted" to challengesCompleted,
                    "level" to level,
                    "bestStreak" to bestStreak,
                    "maxConsecutiveDays" to maxConsecutiveDays,
                    "updatedAt" to System.currentTimeMillis()
                )

                docRef. update(updates).await()
            } else {
                // Documento no existe, crear uno nuevo con score 0
                val firebaseUid = getCurrentFirebaseUid() ?: ""
                val data = hashMapOf(
                    "oduserId" to oduserId,
                    "firebaseUid" to firebaseUid,
                    "name" to name,
                    "avatarRes" to avatarRes,
                    "score" to 0,
                    "generatedAvatarId" to generatedAvatarId,
                    "bannerColorId" to bannerColorId,
                    "highScore" to highScore,
                    "totalPops" to totalPops,
                    "bestClickPercent" to bestClickPercent,
                    "challengesCompleted" to challengesCompleted,
                    "level" to level,
                    "bestStreak" to bestStreak,
                    "maxConsecutiveDays" to maxConsecutiveDays,
                    "updatedAt" to System. currentTimeMillis()
                )
                docRef.set(data).await()
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Actualiza campos específicos del perfil en tiempo real
     * Método optimizado para actualizaciones parciales
     */
    suspend fun updateProfileField(oduserId: String, fieldName: String, value: Any): Boolean {
        return try {
            val docRef = firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_LEADERBOARD)
                .document(oduserId)

            val existingDoc = docRef.get().await()

            if (existingDoc.exists()) {
                val updates = hashMapOf<String, Any>(
                    fieldName to value,
                    "updatedAt" to System.currentTimeMillis()
                )
                docRef.update(updates).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Actualiza múltiples campos del perfil
     */
    suspend fun updateProfileFields(oduserId: String, fields: Map<String, Any>): Boolean {
        return try {
            val docRef = firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_LEADERBOARD)
                .document(oduserId)

            val existingDoc = docRef.get().await()

            if (existingDoc.exists()) {
                val updates = fields.toMutableMap()
                updates["updatedAt"] = System. currentTimeMillis()
                docRef. update(updates).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Verifica si el jugador existe en el leaderboard
     */
    suspend fun playerExistsInLeaderboard(oduserId: String): Boolean {
        return try {
            val doc = firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_LEADERBOARD)
                .document(oduserId)
                .get()
                .await()

            doc.exists()
        } catch (e:  Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtiene el leaderboard como Flow (tiempo real)
     */
    fun getLeaderboardFlow(): Flow<List<FirebaseTournamentEntry>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_TOURNAMENTS)
            .document(DOC_CURRENT)
            .collection(COLLECTION_LEADERBOARD)
            .orderBy("score", Query.Direction. DESCENDING)
            .limit(LEADERBOARD_LIMIT)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // En caso de error, enviamos lista vacía
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val entries = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        FirebaseTournamentEntry(
                            oduserId = doc. getString("oduserId") ?: "",
                            firebaseUid = doc.getString("firebaseUid") ?: "",
                            name = doc.getString("name") ?: "Player",
                            avatarRes = doc. getLong("avatarRes")?.toInt() ?: 0,
                            score = doc.getLong("score")?.toInt() ?: 0,
                            generatedAvatarId = doc. getLong("generatedAvatarId")?.toInt() ?: -1,
                            bannerColorId = doc.getLong("bannerColorId")?.toInt() ?: 0,
                            highScore = doc. getLong("highScore")?.toInt() ?: 0,
                            totalPops = doc.getLong("totalPops")?.toInt() ?: 0,
                            bestClickPercent = doc. getLong("bestClickPercent")?.toInt() ?: 0,
                            challengesCompleted = doc.getLong("challengesCompleted")?.toInt() ?: 0,
                            level = doc.getLong("level")?.toInt() ?: 1,
                            bestStreak = doc. getLong("bestStreak")?.toInt() ?: 0,
                            maxConsecutiveDays = doc.getLong("maxConsecutiveDays")?.toInt() ?: 0,
                            updatedAt = doc. getLong("updatedAt") ?: 0L
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(entries)
            }

        awaitClose {
            listenerRegistration. remove()
        }
    }

    /**
     * Obtiene el leaderboard una sola vez (no en tiempo real)
     */
    suspend fun getLeaderboardOnce(): List<FirebaseTournamentEntry> {
        return try {
            val snapshot = firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_LEADERBOARD)
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(LEADERBOARD_LIMIT)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    FirebaseTournamentEntry(
                        oduserId = doc.getString("oduserId") ?: "",
                        firebaseUid = doc.getString("firebaseUid") ?: "",
                        name = doc.getString("name") ?: "Player",
                        avatarRes = doc.getLong("avatarRes")?.toInt() ?: 0,
                        score = doc.getLong("score")?.toInt() ?: 0,
                        generatedAvatarId = doc. getLong("generatedAvatarId")?.toInt() ?: -1,
                        bannerColorId = doc.getLong("bannerColorId")?.toInt() ?: 0,
                        highScore = doc.getLong("highScore")?.toInt() ?: 0,
                        totalPops = doc.getLong("totalPops")?.toInt() ?: 0,
                        bestClickPercent = doc.getLong("bestClickPercent")?.toInt() ?: 0,
                        challengesCompleted = doc.getLong("challengesCompleted")?.toInt() ?: 0,
                        level = doc. getLong("level")?.toInt() ?: 1,
                        bestStreak = doc.getLong("bestStreak")?.toInt() ?: 0,
                        maxConsecutiveDays = doc.getLong("maxConsecutiveDays")?.toInt() ?: 0,
                        updatedAt = doc.getLong("updatedAt") ?: 0L
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Obtiene la entrada de un jugador específico
     */
    suspend fun getPlayerEntry(oduserId:  String): FirebaseTournamentEntry? {
        return try {
            val doc = firestore
                .collection(COLLECTION_TOURNAMENTS)
                .document(DOC_CURRENT)
                .collection(COLLECTION_LEADERBOARD)
                .document(oduserId)
                .get()
                .await()

            if (doc.exists()) {
                FirebaseTournamentEntry(
                    oduserId = doc. getString("oduserId") ?: "",
                    firebaseUid = doc.getString("firebaseUid") ?: "",
                    name = doc.getString("name") ?: "Player",
                    avatarRes = doc.getLong("avatarRes")?.toInt() ?: 0,
                    score = doc.getLong("score")?.toInt() ?: 0,
                    generatedAvatarId = doc.getLong("generatedAvatarId")?.toInt() ?: -1,
                    bannerColorId = doc.getLong("bannerColorId")?.toInt() ?: 0,
                    highScore = doc.getLong("highScore")?.toInt() ?: 0,
                    totalPops = doc.getLong("totalPops")?.toInt() ?: 0,
                    bestClickPercent = doc.getLong("bestClickPercent")?.toInt() ?: 0,
                    challengesCompleted = doc.getLong("challengesCompleted")?.toInt() ?: 0,
                    level = doc.getLong("level")?.toInt() ?: 1,
                    bestStreak = doc.getLong("bestStreak")?.toInt() ?: 0,
                    maxConsecutiveDays = doc.getLong("maxConsecutiveDays")?.toInt() ?: 0,
                    updatedAt = doc.getLong("updatedAt") ?: 0L
                )
            } else {
                null
            }
        } catch (e:  Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene el rank de un jugador específico
     */
    suspend fun getPlayerRank(oduserId: String): Int {
        val leaderboard = getLeaderboardOnce()
        val index = leaderboard. indexOfFirst { it. oduserId == oduserId }
        return if (index >= 0) index + 1 else leaderboard.size + 1
    }
}

// ==================== DATA CLASSES ====================

data class FirebaseTournamentEntry(
    val oduserId: String,
    val firebaseUid: String,
    val name: String,
    val avatarRes: Int,
    val score: Int,
    val generatedAvatarId: Int,
    val bannerColorId: Int,
    val highScore: Int,
    val totalPops: Int,
    val bestClickPercent: Int,
    val challengesCompleted: Int,
    val level:  Int,
    val bestStreak: Int,
    val maxConsecutiveDays: Int,
    val updatedAt:  Long
)

data class TournamentInfo(
    val epochMillis: Long,
    val tournamentId: String,
    val createdAt: Long
)