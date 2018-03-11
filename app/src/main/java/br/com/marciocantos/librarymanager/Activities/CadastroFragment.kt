package br.com.marciocantos.librarymanager.Activities

import android.os.Bundle
import android.app.Fragment
import android.app.ProgressDialog
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.marciocantos.librarymanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_cadastro.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CadastroFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CadastroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CadastroFragment : Fragment() {

    var controleLayout: Boolean = false;

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        initialise()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cadastro, container, false)
    }

    private fun initialise() {

        etNome = et_Nome
        etEmail = et_Email
        etSenha = et_Password
        btCadastro = btn_Cadastro
        mProgressBar = ProgressDialog(this.context)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

        btCadastro!!.setOnClickListener { createNewAccount() }

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
                    .addOnCompleteListener(activity) { task ->
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
                            Toast.makeText(this.context, "Falha na Autenticaçao", Toast.LENGTH_SHORT).show()
                        }

                    }


        } else {
            Toast.makeText(this.context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfoAndUi() {
        val intent = Intent(this.context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun checkEmail() {
        val mUser = mAuth!!.currentUser
        mUser!!.sendEmailVerification()
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this.context,
                                "E-mail de verificação enviado para " + mUser.getEmail(),
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.exception)
                        Toast.makeText(this.context,
                                "Falha ao enviar e-mail de verificação.",
                                Toast.LENGTH_SHORT).show()
                    }
                }


    }
}
