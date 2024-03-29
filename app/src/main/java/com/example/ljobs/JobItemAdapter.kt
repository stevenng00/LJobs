package com.example.ljobs

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.ljobs.Job.JobEntity
import com.example.ljobs.Job.JobViewModel
import com.example.ljobs.Session.LoginPref
import com.example.ljobs.User.UserViewModel
import com.example.ljobs.databinding.JobItemRowBinding

class JobItemAdapter(private val onClickListener: SelectJobOnClickListener, val owner: ViewModelStoreOwner, val context: Context): RecyclerView.Adapter<JobItemAdapter.ViewHolder>(){

    private var jobList = emptyList<JobEntity>()
    lateinit var session : LoginPref

    class ViewHolder(val binding: JobItemRowBinding) : RecyclerView.ViewHolder(binding.root){
        val jobTitle = binding.jobTitle
        val location = binding.location
        val salary = binding.salary
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return JobItemAdapter.ViewHolder(
            JobItemRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userViewModel = ViewModelProvider(owner).get(UserViewModel::class.java)
        val jobViewModel = ViewModelProvider(owner).get(JobViewModel::class.java)

        session = LoginPref(context!!)
        var user:HashMap<String,String> = session.getUserDetails()
        val email = user.get(LoginPref.KEY_EMAIL).toString()


        val jobItem = jobList[position]
        holder.jobTitle.text = jobItem.title
        holder.location.text = jobItem.location
        holder.salary.text = jobItem.salary

        val img = userViewModel.fetchByEmail(jobItem.email!!).image
        if(img!=null){
            val bmp = getImage(img!!)
            holder.binding.tvProfilePic.visibility = View.GONE
            holder.binding.image.visibility = View.VISIBLE
            holder.binding.image.setImageBitmap(bmp)
        }
        else{
            holder.binding.tvProfilePic.text = userViewModel.fetchByEmail(jobItem.email!!).name?.uppercase()
        }

        holder.binding.itemLayout.setOnClickListener{
            val intent = Intent(holder.itemView.context, JobDescActivity::class.java)
            intent.putExtra("ID", jobItem.id.toInt())
            intent.putExtra("EMAIL", jobItem.email.toString())
            intent.putExtra("TITLE", jobItem.title.toString())
            intent.putExtra("EDUREQUIREMENT", jobItem.eduRequirement.toString())
            intent.putExtra("SALARY", jobItem.salary.toString())
            intent.putExtra("DESCRIPTION", jobItem.desc.toString())
            intent.putExtra("LOCATION", jobItem.location.toString())
            intent.putExtra("TYPE", jobItem.type.toString())
            intent.putExtra("COMPANYINFO", jobItem.companyInfo.toString())

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    internal fun setJobList(jobEntity: List<JobEntity>) {
        this.jobList = jobEntity
        notifyDataSetChanged()
    }

    fun getImage(image: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    class SelectJobOnClickListener (val clickListener: (jobItem: JobEntity) -> Unit) {
        fun onClick(jobItem: JobEntity) = clickListener(jobItem)
    }


}