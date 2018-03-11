package br.com.marciocantos.librarymanager.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.marciocantos.librarymanager.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*


class ForgotPasswordActivity : AppCompatActivity() {

    private val TAG = "ForgotPasswordActivity"

    //Elementos UI
    private var etEmail: EditText? = null
    private var btnEnviar: Button? = null

    //Firebase
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initialise()
    }

    private fun initialise(){

        etEmail = campoEmail as EditText
        btnEnviar = btnSubmit as Button

        mAuth = FirebaseAuth.getInstance()

        btnEnviar!!.setOnClickListener { enviarEmailResetSenha()}

    }

    private fun enviarEmailResetSenha(){

        val email = etEmail?.text.toString()

        if(!TextUtils.isEmpty(email)){
            mAuth!!
                    .sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            val message = "E-mail enviado."
                            Log.d(TAG, message)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            updateUI()
                        }else{
                            Log.w(TAG, task.exception!!.message)
                            Toast.makeText(this, "Nenhum usuário encontrado com esse email!", Toast.LENGTH_SHORT).show()
                        }
                    }
        }else{
            Toast.makeText(this, "Informe um e-mail", Toast.LENGTH_SHORT).show()
        }
    }

    //Após informar um email válido, o usuário é reencaminhado para a tela de login
    private fun updateUI(){
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}
