package com.example.task3

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.task3.adapters.DialogAdapter
import com.example.task3.adapters.RecyclerAdapter
import com.example.task3.databinding.ActivityMainBinding
import com.example.task3.room.Contact
import com.example.task3.room.ContactDao
import com.example.task3.room.ContactDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: ContactDatabase
    private lateinit var notifyManager: NotificationManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: Dialog
    private lateinit var listView: RecyclerView
    private var contactDao: ContactDao? = null
    private var contactNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initHandlers()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initHandlers() {
        val recyclerView: RecyclerView = binding.list
        val contactsAdapter = RecyclerAdapter(ContactsList) {
            CoroutineScope(Dispatchers.Default).launch {
                    contactDao?.insert(ContactsList[it])
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Contact save", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        recyclerView.adapter = contactsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapterDialog = DialogAdapter(roomContactsList) {
            sharedPreferences.edit().putString(key, it.number).apply()
            binding.sharedName.text = getString(R.string.contact_name).plus("${it.firstName?.plus(" ") ?: ""}${it.lastName ?: ""}")
            name_value = getString(R.string.contact_name).plus("${it.firstName?.plus(" ") ?: ""}${it.lastName ?: ""}")
            if (it.number != null) {
                binding.sharedNumber.text = getString(R.string.contact_number).plus(it.number)
                number_value = getString(R.string.contact_number).plus(it.number)
            } else binding.sharedNumber.text = null
            if (it.email != null) {
                binding.sharedEmail.text = getString(R.string.contact_email).plus(it.email)
                email_value = getString(R.string.contact_email).plus(it.email)
            } else binding.sharedEmail.text = null
            dialog.dismiss()
        }
        listView.adapter = adapterDialog

        binding.chooseContact.setOnClickListener() {
            checkPermission()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                CoroutineScope(Dispatchers.Default).launch {
                    val list = getContacts()
                    runOnUiThread {
                        ContactsList.clear()
                        ContactsList.addAll(list)
                        contactsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        binding.showContacts.setOnClickListener() {
            roomContactsList.clear()
            if (contactDao != null) {
                CoroutineScope(Dispatchers.Default).launch {
                    roomContactsList.addAll(contactDao!!.getAll())
                    runOnUiThread() {
                        adapterDialog.notifyDataSetChanged()
                        dialog.show()
                    }
                }
            }
        }
        binding.showSp.setOnClickListener()
        {
            contactNumber = sharedPreferences.getString(key, "")
            var snack: String = "No contact saved"
            if (contactNumber != "") {
                snack = getString(R.string.contact_number).plus(contactNumber)
            }
            val snackbar = Snackbar.make(binding.root, snack, Snackbar.LENGTH_LONG)
            snackbar.setAction("Close", View.OnClickListener {
                snackbar.dismiss()
            })
            snackbar.show()

        }
    }

    private fun init() {
        db = ContactDatabase.getInstance(this)
        contactDao = db.contactDao()
        sharedPreferences = this.getSharedPreferences("com.example.task3", Context.MODE_PRIVATE)
        makeMenu()
        makeDialog()
        notifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        binding.sharedName.text = name_value
        binding.sharedNumber.text = number_value
        binding.sharedEmail.text = email_value
        checkPermission()
    }

    private fun makeMenu() {
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun checkPermission() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
            (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                1
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.finish) {
            sendNotification()
        }
        return true
    }

    @SuppressLint("Range", "Recycle")
    fun getContacts(): MutableList<Contact> {
        val list: MutableList<Contact> = ArrayList<Contact>().toMutableList()
        val cursor =
            contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )
        while (cursor?.moveToNext() == true) {
            val id =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            var firstName: String? = null
            var lastName: String? = null
            val nameCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = " + id,
                arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE),null)
            if (nameCursor?.moveToNext() == true) with(nameCursor) {
                firstName = getString(getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                lastName = getString(getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
                close()
            }

            var email: String? = null
            val emailCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                arrayOf(id),
                null
            )
            if (emailCursor?.moveToNext() == true) {
                email =
                    emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                emailCursor.close()
            }

            var number: String? = null
            val phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(id),
                null
            )
            if (phoneCursor?.moveToNext() == true) {
                number =
                    phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                phoneCursor.close()
                println()
            }
            list += (Contact(
                id,
                firstName,
                lastName,
                number,
                email
            ))
        }
        return list
    }

    private fun sendNotification() {
        val notification = Notification(this, notifyManager)
        contactNumber = sharedPreferences.getString(key, "")
        CoroutineScope(Dispatchers.Default).launch {
            val contact = contactNumber?.let { contactDao?.getByNumber(it) }
            runOnUiThread {
                notification.send(contact)
            }
        }
    }

    private fun makeDialog() {
        listView = RecyclerView(this)
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setView(listView)
        builder.setTitle("Contacts")
        listView.layoutManager = LinearLayoutManager(this)
        dialog = builder.create()
    }

    companion object {
        var ContactsList: MutableList<Contact> = ArrayList<Contact>().toMutableList()
        var roomContactsList: MutableList<Contact> = ArrayList<Contact>().toMutableList()
        const val key = "contact_number"
        var name_value: String? = null
        var number_value: String? = null
        var email_value: String? = null
    }
}