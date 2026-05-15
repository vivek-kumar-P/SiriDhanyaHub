package com.mindmatrix.siridhanyahub.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mindmatrix.siridhanyahub.data.local.entity.ConsumerMilletRequestEntity
import com.mindmatrix.siridhanyahub.data.local.entity.FarmerStockListingEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirestoreMarketplaceRepository @Inject constructor() {

    private val db = Firebase.database(
        "https://siridhanyahub-2c46c-default-rtdb.firebaseio.com"
    ).apply {
        setPersistenceEnabled(true) // enables offline cache
    }

    private val farmerListings = db.getReference("farmer_listings")
    private val consumerRequests = db.getReference("consumer_requests")

    init {
        // Keep these references synced even when no listener is active
        farmerListings.keepSynced(true)
        consumerRequests.keepSynced(true)
    }

    suspend fun publishFarmerListing(
        listing: FarmerStockListingEntity,
        farmerName: String,
        farmerMobile: String,
        farmerDistrict: String
    ): Result<Unit> = runCatching {
        farmerListings.child(listing.farmerUserId).setValue(
            mapOf(
                "farmerUserId" to listing.farmerUserId,
                "farmerName" to farmerName,
                "farmerMobile" to farmerMobile,
                "farmerDistrict" to farmerDistrict,
                "milletType" to listing.milletType,
                "quantityAvailableKg" to listing.quantityAvailableKg,
                "marketCity" to listing.marketCity,
                "marketPlace" to listing.marketPlace,
                "selectedMarketRate" to listing.selectedMarketRate,
                "growthDurationDays" to listing.growthDurationDays,
                "grownArea" to listing.grownArea,
                "stockNote" to listing.stockNote,
                "isActive" to true,
                "updatedAt" to listing.updatedAt
            )
        ).await()
    }

    suspend fun publishConsumerRequest(
        request: ConsumerMilletRequestEntity,
        consumerName: String,
        consumerMobile: String,
        consumerDistrict: String
    ): Result<Unit> = runCatching {
        consumerRequests.child(request.consumerUserId).setValue(
            mapOf(
                "consumerUserId" to request.consumerUserId,
                "consumerName" to consumerName,
                "consumerMobile" to consumerMobile,
                "consumerDistrict" to consumerDistrict,
                "milletType" to request.milletType,
                "quantityKg" to request.quantityKg,
                "neededAtMillis" to request.neededAtMillis,
                "consumerLocation" to request.consumerLocation,
                "preferredSourceLocation" to request.preferredSourceLocation,
                "purpose" to (request.purpose ?: ""),
                "status" to request.status,
                "createdAt" to request.createdAt
            )
        ).await()
    }

    suspend fun updateConsumerRequestStatus(
        consumerUserId: String,
        status: String
    ): Result<Unit> = runCatching {
        consumerRequests.child(consumerUserId).child("status").setValue(status).await()
    }

    fun observeAllActiveConsumerRequests(): Flow<List<FirestoreConsumerRequest>> = callbackFlow {
        trySend(emptyList()) // emit empty immediately so UI doesn't hang

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<FirestoreConsumerRequest>()
                for (doc in snapshot.children) {
                    try {
                        val status = doc.child("status").getValue(String::class.java) ?: continue
                        if (status != "ACTIVE") continue
                        val consumerUserId = doc.child("consumerUserId")
                            .getValue(String::class.java) ?: continue
                        items.add(
                            FirestoreConsumerRequest(
                                consumerUserId = consumerUserId,
                                consumerName = doc.child("consumerName")
                                    .getValue(String::class.java) ?: "",
                                consumerMobile = doc.child("consumerMobile")
                                    .getValue(String::class.java) ?: "",
                                consumerDistrict = doc.child("consumerDistrict")
                                    .getValue(String::class.java) ?: "",
                                milletType = doc.child("milletType")
                                    .getValue(String::class.java) ?: "",
                                quantityKg = (doc.child("quantityKg")
                                    .getValue(Long::class.java) ?: 0L).toInt(),
                                neededAtMillis = doc.child("neededAtMillis")
                                    .getValue(Long::class.java) ?: 0L,
                                consumerLocation = doc.child("consumerLocation")
                                    .getValue(String::class.java) ?: "",
                                preferredSourceLocation = doc.child("preferredSourceLocation")
                                    .getValue(String::class.java) ?: "",
                                purpose = doc.child("purpose")
                                    .getValue(String::class.java) ?: "",
                                status = status,
                                createdAt = doc.child("createdAt")
                                    .getValue(Long::class.java) ?: 0L
                            )
                        )
                    } catch (e: Exception) {
                        // skip malformed entries
                    }
                }
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }

        consumerRequests.addValueEventListener(listener)
        awaitClose { consumerRequests.removeEventListener(listener) }
    }

    fun observeAllActiveFarmerListings(): Flow<List<FirestoreFarmerListing>> = callbackFlow {
        trySend(emptyList()) // emit empty immediately so UI doesn't hang

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<FirestoreFarmerListing>()
                for (doc in snapshot.children) {
                    try {
                        // Handle isActive as any type Firebase might store it as
                        val isActiveRaw = doc.child("isActive").value
                        val isActive = isActiveRaw == true ||
                                isActiveRaw?.toString() == "true"
                        if (!isActive) continue

                        val farmerUserId = doc.child("farmerUserId")
                            .getValue(String::class.java) ?: continue

                        items.add(
                            FirestoreFarmerListing(
                                farmerUserId = farmerUserId,
                                farmerName = doc.child("farmerName")
                                    .getValue(String::class.java) ?: "",
                                farmerMobile = doc.child("farmerMobile")
                                    .getValue(String::class.java) ?: "",
                                farmerDistrict = doc.child("farmerDistrict")
                                    .getValue(String::class.java) ?: "",
                                milletType = doc.child("milletType")
                                    .getValue(String::class.java) ?: "",
                                quantityAvailableKg = (doc.child("quantityAvailableKg")
                                    .getValue(Long::class.java) ?: 0L).toInt(),
                                marketCity = doc.child("marketCity")
                                    .getValue(String::class.java) ?: "",
                                marketPlace = doc.child("marketPlace")
                                    .getValue(String::class.java) ?: "",
                                selectedMarketRate = doc.child("selectedMarketRate")
                                    .getValue(Double::class.java) ?: 0.0,
                                growthDurationDays = (doc.child("growthDurationDays")
                                    .getValue(Long::class.java) ?: 0L).toInt(),
                                grownArea = doc.child("grownArea")
                                    .getValue(String::class.java) ?: "",
                                stockNote = doc.child("stockNote")
                                    .getValue(String::class.java) ?: "",
                                isActive = isActive,
                                updatedAt = doc.child("updatedAt")
                                    .getValue(Long::class.java) ?: 0L
                            )
                        )
                    } catch (e: Exception) {
                        // skip malformed entries
                    }
                }
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }

        farmerListings.addValueEventListener(listener)
        awaitClose { farmerListings.removeEventListener(listener) }
    }


    suspend fun updateFarmerQuantityAndRate(
        farmerUserId: String,
        newQuantity: Int,
        newRate: Double
    ): Result<Unit> = runCatching {
        farmerListings.child(farmerUserId).updateChildren(
            mapOf(
                "quantityAvailableKg" to newQuantity,
                "selectedMarketRate" to newRate,
                "isActive" to (newQuantity > 0),
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun publishFeedbackToFirebase(
        farmerUserId: String,
        consumerName: String,
        rating: Int,
        comment: String,
        milletType: String,
        quantityKg: Int
    ): Result<Unit> = runCatching {
        val feedbackRef = db.getReference("farmer_feedback").child(farmerUserId).push()
        feedbackRef.setValue(
            mapOf(
                "consumerName" to consumerName,
                "rating" to rating,
                "comment" to comment,
                "milletType" to milletType,
                "quantityKg" to quantityKg,
                "createdAt" to System.currentTimeMillis()
            )
        ).await()
    }
}

data class FirestoreConsumerRequest(
    val consumerUserId: String,
    val consumerName: String,
    val consumerMobile: String,
    val consumerDistrict: String,
    val milletType: String,
    val quantityKg: Int,
    val neededAtMillis: Long,
    val consumerLocation: String,
    val preferredSourceLocation: String,
    val purpose: String,
    val status: String,
    val createdAt: Long
)

data class FirestoreFarmerListing(
    val farmerUserId: String,
    val farmerName: String,
    val farmerMobile: String,
    val farmerDistrict: String,
    val milletType: String,
    val quantityAvailableKg: Int,
    val marketCity: String,
    val marketPlace: String,
    val selectedMarketRate: Double,
    val growthDurationDays: Int,
    val grownArea: String,
    val stockNote: String,
    val isActive: Boolean,
    val updatedAt: Long
)