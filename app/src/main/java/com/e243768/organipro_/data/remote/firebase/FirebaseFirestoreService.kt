package com.e243768.organipro_.data.remote.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Obtener un documento por ID
     */
    suspend fun <T> getDocument(
        collection: String,
        documentId: String,
        clazz: Class<T>
    ): T? {
        return try {
            val snapshot = firestore.collection(collection)
                .document(documentId)
                .get()
                .await()

            snapshot.toObject(clazz)
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Obtener un documento por ID con Flow (tiempo real)
     */
    fun <T> getDocumentFlow(
        collection: String,
        documentId: String,
        clazz: Class<T>
    ): Flow<T?> = callbackFlow {
        val listener = firestore.collection(collection)
            .document(documentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val data = snapshot?.toObject(clazz)
                trySend(data)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtener múltiples documentos por query
     */
    suspend fun <T> getDocuments(
        collection: String,
        clazz: Class<T>,
        queryBuilder: (Query) -> Query = { it }
    ): List<T> {
        return try {
            val baseQuery = firestore.collection(collection)
            val query = queryBuilder(baseQuery)

            val snapshot = query.get().await()
            snapshot.documents.mapNotNull { it.toObject(clazz) }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Obtener documentos con Flow (tiempo real)
     */
    fun <T> getDocumentsFlow(
        collection: String,
        clazz: Class<T>,
        queryBuilder: (Query) -> Query = { it }
    ): Flow<List<T>> = callbackFlow {
        val baseQuery = firestore.collection(collection)
        val query = queryBuilder(baseQuery)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val data = snapshot?.documents?.mapNotNull { it.toObject(clazz) } ?: emptyList()
            trySend(data)
        }

        awaitClose { listener.remove() }
    }

    /**
     * Crear o actualizar un documento
     */
    suspend fun setDocument(
        collection: String,
        documentId: String,
        data: Any,
        merge: Boolean = false
    ) {
        try {
            val options = if (merge) SetOptions.merge() else SetOptions.overwrite()
            firestore.collection(collection)
                .document(documentId)
                .set(data, options)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Actualizar campos específicos de un documento
     */
    suspend fun updateDocument(
        collection: String,
        documentId: String,
        updates: Map<String, Any>
    ) {
        try {
            firestore.collection(collection)
                .document(documentId)
                .update(updates)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Eliminar un documento
     */
    suspend fun deleteDocument(
        collection: String,
        documentId: String
    ) {
        try {
            firestore.collection(collection)
                .document(documentId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Eliminar múltiples documentos por query
     */
    suspend fun deleteDocuments(
        collection: String,
        queryBuilder: (Query) -> Query
    ) {
        try {
            val baseQuery = firestore.collection(collection)
            val query = queryBuilder(baseQuery)

            val snapshot = query.get().await()
            val batch = firestore.batch()

            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.commit().await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Ejecutar una transacción
     */
    suspend fun <T> runTransaction(
        block: suspend (com.google.firebase.firestore.Transaction) -> T
    ): T {
        return try {
            firestore.runTransaction { transaction ->
                kotlinx.coroutines.runBlocking {
                    block(transaction)
                }
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Batch write (múltiples operaciones en una sola escritura)
     */
    suspend fun batchWrite(
        operations: suspend (com.google.firebase.firestore.WriteBatch) -> Unit
    ) {
        try {
            val batch = firestore.batch()
            operations(batch)
            batch.commit().await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Contar documentos en una colección
     */
    suspend fun countDocuments(
        collection: String,
        queryBuilder: (Query) -> Query = { it }
    ): Int {
        return try {
            val baseQuery = firestore.collection(collection)
            val query = queryBuilder(baseQuery)
            val snapshot = query.get().await()
            snapshot.size()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Verificar si un documento existe
     */
    suspend fun documentExists(
        collection: String,
        documentId: String
    ): Boolean {
        return try {
            val snapshot = firestore.collection(collection)
                .document(documentId)
                .get()
                .await()
            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtener referencia a una colección
     */
    fun getCollectionReference(collection: String) = firestore.collection(collection)

    /**
     * Obtener referencia a un documento
     */
    fun getDocumentReference(collection: String, documentId: String) =
        firestore.collection(collection).document(documentId)
}