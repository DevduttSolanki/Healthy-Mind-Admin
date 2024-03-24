package com.example.healthymindadmin

import android.app.Dialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.healthymindadmin.databinding.FragmentHomeBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var addCategoryImg: CircleImageView? = null
    var txtEnterCategoryName: EditText? = null
    var btnUploadCategory: Button? = null
    var viewFetchImg: View? = null
    var imageUri: Uri? = null
    var dialog: Dialog? = null
    var list: ArrayList<CategoryModel>? = null
    var adapter: CategoryAdapter? = null
    var i = 0
    var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)


        //progressDialogLoad.show();
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()


    }

}