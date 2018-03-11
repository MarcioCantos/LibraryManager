package br.com.marciocantos.librarymanager.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.marciocantos.librarymanager.R
import com.google.firebase.auth.FirebaseAuth



class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance();
    }
}
