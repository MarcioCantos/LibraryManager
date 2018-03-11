package br.com.marciocantos.librarymanager.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import br.com.marciocantos.librarymanager.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    //Firebase
    private var mAuth: FirebaseAuth? = null

    private val TAG = "LoginActivity"

    //Variáveis globais
    private var email: String? = null
    private var senha: String? = null

    //Elementos UI
    private var txtRecuperarSenha: TextView? = null
    private var etEmail: EditText? = null
    private var etSenha: EditText? = null
    private var btnLogin: Button? = null
    private var btnCadastrar: Button? = null
    private var mProgressBar: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialise()
    }

    private fun initialise(){
        txtRecuperarSenha = btnRecoverPassword as TextView
        etEmail = campoEmail as EditText
        etSenha = campoSenha as EditText
        btnLogin = btnEntrar as Button
        btnCadastrar = btnRegistrar as Button
        mProgressBar = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance()

        //Manda para a activity de Recuperar a Senha
        txtRecuperarSenha!!
                .setOnClickListener{
                    startActivity(
                            Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    )
                }

        //Manda para a activity de Cadastro
        btnCadastrar!!
                .setOnClickListener {
                    startActivity(
                            Intent(this@LoginActivity, CreateAccountActivity::class.java)
                    ) }

        //Efetua o login
        btnLogin!!.setOnClickListener{loginUser()}

    }

    private fun loginUser(){
        email = etEmail?.text.toString()
        senha = etSenha?.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(senha)){

            mProgressBar!!.setMessage("Registrando Usuário")
            mProgressBar!!.show()

            Log.d(TAG,"Logging in user")

            mAuth!!.signInWithEmailAndPassword(email!!, senha!!)
                    .addOnCompleteListener(this) { task ->

                        mProgressBar!!.hide()

                        if(task.isSuccessful){
                            // Sucesso no login, atualizando UI com as informações do usuário
                            Log.d(TAG, "signInWithEmail:success")
                            updateUI()
                        }else{
                            //Se o login falhar, mostra mensagem ao usuário
                            Log.e(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this@LoginActivity, "Falha no login", Toast.LENGTH_SHORT).show()
                        }
                    }
        }else{
            Toast.makeText(this@LoginActivity, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    //Ao efetuar o login, envia o usuário a tela principal da APP
    private fun updateUI(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}
