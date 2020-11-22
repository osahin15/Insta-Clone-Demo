package com.example.android.kotlininstaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*
import java.sql.Timestamp

class FeedActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private  lateinit var db:FirebaseFirestore

    var userEmailFromFb : ArrayList<String> = ArrayList()
    var userCommentFromFb : ArrayList<String> = ArrayList()
    var userImageFromFb : ArrayList<String> = ArrayList()

    var adapter: FeedRecyclerAdapter? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post){
            //add post activity
            val intent = Intent(applicationContext,UploadActivity::class.java)
            startActivity(intent)

        }else if (item.itemId == R.id.logout){
            //LogOut
            auth.signOut()
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        getDataFromFirestore()

        //recyclerview
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = FeedRecyclerAdapter(userEmailFromFb,userCommentFromFb,userImageFromFb)
        recyclerView.adapter = adapter



    }

    fun getDataFromFirestore(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error!=null){
                Toast.makeText(applicationContext,error.localizedMessage?.toString(),Toast.LENGTH_LONG).show()
            }else{
                if (value !=null){
                    if (!value.isEmpty){
                        userImageFromFb.clear()
                        userCommentFromFb.clear()
                        userEmailFromFb.clear()

                        val documents = value.documents
                        for (document in documents){
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val timestamp = document.get("date") as com.google.firebase.Timestamp
                            val date = timestamp.toDate()

                            userEmailFromFb.add(userEmail)
                            userCommentFromFb.add(comment)
                            userImageFromFb.add(downloadUrl)

                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

}
