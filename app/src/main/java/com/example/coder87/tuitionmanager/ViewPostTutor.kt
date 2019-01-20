package com.example.coder87.tuitionmanager

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.post_demo_tutor.view.*
import java.text.SimpleDateFormat
import java.util.*

class ViewPostTutor : Activity() {
    private var postsTutor=ArrayList<PostTutor>()
    private lateinit var mp: MediaPlayer
    var signer=""
    var sigenrId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post_tutor)
        getValues()
        retrievepostsTutor()
        prepareHomePagePostTutor()
    }

    private fun retrievepostsTutor() {

        postsTutor.clear()
        var dataBase= FirebaseDatabase.getInstance().getReference("Tuition Wanted")
        dataBase.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for(h in p0.children){
                    var name=h.child("Name").getValue().toString()
                    var location=h.child("Location").getValue().toString()
                    var clas=h.child("Class").getValue().toString()
                    var subject=h.child("Subjects").getValue().toString()
                    var day=h.child("Days").getValue().toString()
                    var salary=h.child("Salary").getValue().toString()
                    var thumbsup=h.child("Thumbs Up").getValue().toString()
                    var thumbsdown=h.child("Thumbs Down").getValue().toString()
                    var phone=h.child("Phone").getValue().toString()
                    val postdate=h.child("Date").getValue().toString()
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val currentDate = sdf.format(Date())

                    var time=getTime(currentDate,postdate)

                    postsTutor.add(PostTutor(phone,name+"  posted "+time,"Tuition Wanted",location,clas,subject,day,salary,thumbsup,thumbsdown))

                }
                prepareHomePagePostTutor()

            }


        })


    }
    private fun prepareHomePagePostTutor() {
        val cardsRecyclerView: RecyclerView = findViewById(R.id.view_post_tutor_holder)
        cardsRecyclerView.layoutManager = LinearLayoutManager(this)
        cardsRecyclerView.adapter = PostTutorAdapter(postsTutor)
    }

    inner class PostTutorAdapter(private val cards : ArrayList<PostTutor>) : RecyclerView.Adapter<PostTutorItemViewHolder>() {

        override fun getItemCount(): Int {
            return cards.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostTutorItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_demo_tutor, parent, false)

            return PostTutorItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: PostTutorItemViewHolder, position: Int) {
            val card = cards[position]

            holder.timePost.text = card.timePost
            holder.typePost.text = card.typePost
            holder.locationPost.text = card.locationPost
            holder.subjectPost.text = card.subjectPost
            holder.classPost.text = card.classPost
            holder.daysPost.text = card.daysPost
            holder.salaryPost.text = card.salaryPost
            holder.thumbUpPost.text=card.thumbUpPost
            holder.thumbDownPost.text=card.thumbDownPost

            holder.thumbUpPost.setOnClickListener {
                playSound()
                val database=FirebaseDatabase.getInstance().getReference("Tuition Wanted").child(card.id)
                database.addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var thumbsUp=p0.child("Thumbs Up").getValue().toString()
                        var like=thumbsUp.toInt()
                        like++
                        database.child("Thumbs Up").setValue(""+like+"")
                        retrievepostsTutor()
                    }

                })
            }
            holder.thumbDownPost.setOnClickListener {
                playSound()
                val database=FirebaseDatabase.getInstance().getReference("Tuition Wanted").child(card.id)
                database.addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var thumbsUp=p0.child("Thumbs Down").getValue().toString()
                        var like=thumbsUp.toInt()
                        like++
                        database.child("Thumbs Down").setValue(""+like+"")
                        retrievepostsTutor()
                    }

                })
            }
            holder.viewProfile.setOnClickListener {
                viewProfileTutor(card.id)
            }
        }
    }


    class PostTutorItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val timePost: TextView=view.post_time_tutor
        val typePost: TextView=view.post_type_tutor
        val locationPost: TextView=view.post_location_tutor
        val classPost: TextView=view.post_class_tutor
        val subjectPost: TextView=view.post_subjects_tutor
        val daysPost: TextView=view.post_days_tutor
        val salaryPost: TextView=view.post_salary_tutor
        val thumbUpPost:TextView=view.post_thumbsup_tutor
        val thumbDownPost:TextView=view.post_thumbsdown_tutor

        val viewProfile: CardView =view.view_profile_tutor

    }
    fun viewProfileTutor(posterId:String){
        val intent=Intent(this,ViewProfileTutor::class.java)
        intent.putExtra(emailPhone,posterId)
        intent.putExtra(type,"Tutor")
        startActivity(intent)
    }
    fun playSound() {
        mp = MediaPlayer.create (this, R.raw.like)
        mp.start()
    }
    fun getValues(){
        signer=intent.getStringExtra(type)
        sigenrId=intent.getStringExtra(emailPhone)
    }
    fun getTime(curr:String,post:String):String{
        var st=StringTokenizer(curr," ")
        var cdate=st.nextToken()
        var st2=StringTokenizer(post," ")
        var pdate=st2.nextToken()
        var st3=StringTokenizer(cdate,"/")
        var st4=StringTokenizer(pdate,"/")
        var cday=st3.nextToken().toInt()
        var pday=st4.nextToken().toInt()
        var cmonth=st3.nextToken().toInt()
        var pmonth=st4.nextToken().toInt()
        var cyear=st3.nextToken().toInt()
        var pyear=st4.nextToken().toInt()

        var day=1
        var month=1
        var year=1

        if(cday>=pday&&cmonth>=pmonth&&cyear>=pyear){
            day=cday-pday
            month=cmonth-pmonth
            year=cyear-pyear
        }
        else{
            if(cyear>pyear){
                if (cmonth >= pmonth) {
                    if (cday < pday) {
                        year = cyear - pyear;
                        month = cmonth - pmonth - 1;
                        day = 31 - pday + cday;
                    }
                }
                else{
                    if (cmonth < pmonth) {
                        if (cday > pday) {
                            year = cyear - pyear - 1;
                            month = 12 - pmonth + cmonth;
                            day = cday - pday;
                        }
                        else
                            if (pday > cday) {
                                year = cyear - pyear - 1;
                                month = 12 - pmonth + cmonth - 1;
                                day = 31 - pday + cday;
                            }
                    }
                }
            }
        }
        var date=""
        if(year>0){

            date=year.toString()+"y ago"

        }
        else if(month>0){
            date=month.toString()+"m ago"

        }
        else{
            if(day==0)
                date="today"
            else
                date=day.toString()+"d ago"
        }

        return date
    }
    fun printToast(s:String){

        val toast = Toast.makeText(this, s, Toast.LENGTH_SHORT)
        toast.show()
    }

}
