package br.com.marciocantos.librarymanager.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import br.com.marciocantos.librarymanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    //Elementos UI
    private var etNome: EditText? = null
    private var etEmail: EditText? = null
    private var etSenha: EditText? = null
    private var btCadastro: Button? = null
    private var mProgressBar: ProgressDialog? = null

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private val TAG = "CreateAccountActivity"
    //global variables
    private var nome: String? = null
    private var email: String? = null
    private var senha: String? = null
    private var btnRetornaLogin: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        initialise()
    }

    private fun initialise() {

        etNome = et_Nome
        etEmail = et_Email
        etSenha = et_Password
        btCadastro = btn_Cadastro
        mProgressBar = ProgressDialog(this)
        btnRetornaLogin = txtJaCadastrado as TextView

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

        btCadastro!!.setOnClickListener { createNewAccount() }

        btnRetornaLogin!!.setOnClickListener {
            startActivity(
                    Intent(this@CreateAccountActivity, LoginActivity::class.java)
            )
        }

    }

    private fun createNewAccount() {
        nome = etNome?.text.toString()
        email = etEmail?.text.toString()
        senha = etSenha?.text.toString()

        if (!TextUtils.isEmpty(nome) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(senha)) {

            //Enquanto o usuário é cadastrado o firebase pode demorar para retornar
            //então mostramos mensagem de que o usuário está sendo registrado
            mProgressBar!!.setMessage("Registrando Usuário...")
            mProgressBar!!.show()

            //Criando usuário no firebase
            mAuth!!
                    .createUserWithEmailAndPassword(email!!, senha!!)
                    .addOnCompleteListener(this) { task ->
                        mProgressBar!!.hide()

                        if (task.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:success")

                            var userId = mAuth!!.currentUser!!.uid

                            //verifica email
                            checkEmail()

                            val currentUserDb = mDatabaseReference!!.child(userId)
                            currentUserDb.child("nome").setValue(nome)

                            updateUserInfoAndUi()
                        } else {
                            //em caso de falha na criação do usuário
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(this, "Falha na Autenticaçao", Toast.LENGTH_SHORT).show()
                        }

                    }


        } else {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfoAndUi() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun checkEmail() {
        val mUser = mAuth!!.currentUser
        mUser!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,
                                "E-mail de verificação enviado para " + mUser.getEmail(),
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.exception)
                        Toast.makeText(this,
                                "Falha ao enviar e-mail de verificação.",
                                Toast.LENGTH_SHORT).show()
                    }
                }


    }
}
