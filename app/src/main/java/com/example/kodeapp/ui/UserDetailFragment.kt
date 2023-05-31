package com.example.kodeapp.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.kodeapp.R
import com.example.kodeapp.appComponent
import com.example.kodeapp.data.model.User
import com.example.kodeapp.databinding.FragmentUserDetailBinding
import com.example.kodeapp.utils.Constants.ARG_USER
import com.example.kodeapp.viewmodel.UserDetailViewModel
import com.example.kodeapp.viewmodel.ViewModelFactory
import javax.inject.Inject

class UserDetailFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailBinding
    private var user: User? = null

    private val viewModel: UserDetailViewModel by viewModels {
        factory.create()
    }

    @Inject
    lateinit var factory: ViewModelFactory.Factory

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_USER, User::class.java)
            } else {
                it.getParcelable(ARG_USER)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false)

        setupUI(user)

        setupToolBar()

        return binding.root
    }

    private fun setupToolBar(){
        binding.topAppBarDetail.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupUI(user: User?){
        if (user != null){
            if (user.avatarUrl.isNotBlank()) {
                Glide.with(binding.userAvatarDetail.context)
                    .load(user.avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.icon_avatar)
                    .error(R.drawable.icon_avatar)
                    .into(binding.userAvatarDetail)
            } else {
                binding.userAvatarDetail.setImageResource(R.drawable.icon_avatar)
            }
            binding.userNameDetail.text = viewModel.convertString(user.firstName, user.lastName)
            binding.phoneNumberDetail.text = user.phone
            binding.birthDateDetail.text = convertDate(user.birthday)
            binding.userTagDetail.text = user.userTag
            binding.userPositionDetail.text = user.position
            binding.ageDetail.text = getAge(user.birthday)
        }
    }

    private fun convertDate(date: String): String {
        return viewModel.formatDate(date)
    }

    private fun getAge(date: String): String {
        val age = viewModel.calculateAge(date)
        return viewModel.convertString(age.toString(), viewModel.getAgeSuffix(age))
    }

    companion object {
        @JvmStatic
        fun newInstance(user: User) =
            UserDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }
}