package com.reg.homeworks.ui.fragments.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.reg.homeworks.R
import com.reg.homeworks.databinding.FragmentAuthBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.task.homework_4.data.local.preferences.PreferencesManager
import com.vicmikhailau.maskededittext.MaskedFormatter
import com.vicmikhailau.maskededittext.MaskedWatcher
import java.util.concurrent.TimeUnit

class AuthFragment : Fragment(R.layout.fragment_auth) {
    private val binding by viewBinding(FragmentAuthBinding::bind)
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val formatter = MaskedFormatter("### ### ###")

    private var phone = ""

    private val preferencesManager by lazy {
        PreferencesManager(
            requireContext().getSharedPreferences(
                "chat.preferences",
                Context.MODE_PRIVATE
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        setMaskToEditText()
        enableButtonDependingOnThePhoneNumberLength()
        sendVerificationCodeAndProceed()
        setupChanged()
    }

    private fun setMaskToEditText() {
        binding.includeAuth.etPhoneNumber.addTextChangedListener(
            MaskedWatcher(
                formatter,
                binding.includeAuth.etPhoneNumber
            )
        )
    }

    private fun enableButtonDependingOnThePhoneNumberLength() {
        binding.includeAuth.etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.includeAuth.btnSendVerificationCode.isEnabled =
                    when (s.toString().length == 11) {
                        true -> {
                            binding.includeAuth.btnSendVerificationCode.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.purple_500
                                )
                            )
                            true
                        }
                        false -> {
                            binding.includeAuth.btnSendVerificationCode.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.gray
                                )
                            )
                            false
                        }
                    }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupChanged() {
        binding.includeAccept.pvVerificationCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == 6) {
                    hideSoftKeyboard()
                    signInWithPhoneAuthCredential(verifyPhoneNumberWithCode(s.toString()))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    fun verifyPhoneNumberWithCode(
        code: String,
    ) =
        PhoneAuthProvider.getCredential(preferencesManager.verificationId.toString(), code)

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) =
        with(binding.includeAccept.pvVerificationCode) {
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        preferencesManager.isAuthenticated = true
                        setHideLineWhenFilled(false)
                        setLineColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        handler.postDelayed({
                            findNavController().navigate(R.id.chatsFragment)
                        }, 1000L)
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            text?.clear()
                            setLineColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.red
                                )
                            )
                            handler.postDelayed({
                                setLineColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.gray
                                    )
                                )
                            }, 2000L)
                        }
                    }
                }
        }


    private fun sendVerificationCodeAndProceed() {
        binding.includeAuth.btnSendVerificationCode.setOnClickListener {
            hideSoftKeyboard()
            phone = binding.includeAuth.tlPhoneNumber.prefixText.toString() + formatter.formatString(
                    binding.includeAuth.etPhoneNumber.text.toString()
                )?.unMaskedString.toString()
            val options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(providePhoneAuthCallbacks())
                    .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun providePhoneAuthCallbacks() =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        requireContext(),
                        e.localizedMessage?.toString() ?: "An error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(
                        requireContext(),
                        e.localizedMessage?.toString() ?: "An error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                Toast.makeText(
                    requireContext(),
                    "Verification code has been sent to your phone number",
                    Toast.LENGTH_LONG
                ).show()
                binding.acceptContainer.isVisible = true
                binding.authContainer.isVisible = false
                preferencesManager.verificationId = verificationId
                saveUserData()
            }
        }

    private fun saveUserData() {
        val uid = firebaseAuth.currentUser?.uid


        if (uid != null) {
            val ref = FirebaseFirestore.getInstance().collection("Users").document(uid)
            val userData = hashMapOf<String, String>()
            userData["phone"] = binding.includeAuth.tlPhoneNumber.prefixText.toString() + formatter.formatString(
                binding.includeAuth.etPhoneNumber.text.toString()
            )?.unMaskedString.toString()

            ref.set(userData).addOnCompleteListener {
                if (it.isSuccessful) {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun Fragment.hideSoftKeyboard() {
        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)

    }
}


