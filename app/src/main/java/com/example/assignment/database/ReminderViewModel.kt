package com.example.assignment.database

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReminderViewModel: ViewModel(){
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val Reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders : StateFlow<List<Reminder>> = Reminders

    init{
        fetchReminders()
    }

    private fun fetchReminders(){
        val username = auth.currentUser?.uid ?: return

        db.collection("users").document(username).collection("reminders")
            .addSnapshotListener { snapshot, error ->
                if(error != null || snapshot == null) return@addSnapshotListener

                val reminderList = snapshot.documents.mapNotNull{
                    doc ->
                    val reminder = doc.toObject(Reminder:: class.java)
                    reminder ?.apply {documentId = doc.id}
                }
                Reminders.value = reminderList
            }
    }

    fun saveReminder(reminder: Reminder) {
        val username = auth.currentUser?.uid ?: return
        val reminderToSave = reminder.copy(username = username)

        val collection = db.collection("users").document(username).collection("reminders")

        if (reminderToSave.documentId.isEmpty()) {
            // Add a new document (Firestore generates the ID automatically)
            collection.add(reminderToSave)
        } else {
            // Update an existing document
            collection.document(reminderToSave.documentId).set(reminderToSave)
        }
    }

    fun deleteReminder(documentId: String) {
        val username = auth.currentUser?.uid ?: return
        db.collection("users").document(username).collection("reminders").document(documentId).delete()
    }
}