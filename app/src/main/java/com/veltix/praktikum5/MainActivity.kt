package com.veltix.praktikum5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView

class MainActivity : LoggedActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: MahasiswaAdapter
    private lateinit var btnUrutan: Button
    private val dataMahasiswa = mutableListOf<Mahasiswa>()

    // State layar: urut berdasarkan NIM (true) atau nama (false).
    private var urutNim = false
    private var jumlahKetuk =  0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        adapter = MahasiswaAdapter(this, dataMahasiswa)
        btnUrutan = findViewById(R.id.btnUrutan)
        val listView = findViewById<ListView>(R.id.listMahasiswa)
        listView.adapter = adapter

        findViewById<Button>(R.id.btnTambah).setOnClickListener {
            startActivity(Intent(this, FormActivity::class.java))
        }

        btnUrutan.setOnClickListener {
            urutNim = !urutNim
            jumlahKetuk++
            loadData()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, dataMahasiswa[position].id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        dataMahasiswa.clear()
        dataMahasiswa.addAll(db.getAllMahasiswa(urutNim))
        adapter.notifyDataSetChanged()
        btnUrutan.text = if (urutNim) "Urut: NIM" else "Urut: Nama"
        Log.i("Data", "Memuat ${dataMahasiswa.size} baris, urutNim=$urutNim")
        findViewById<TextView>(R.id.tvJudul).text =
            "Data Mahasiswa (Urut diketuk $jumlahKetuk kali)"
    }

    // Dipanggil sebelum Activity dihancurkan (rotasi, memori sempit).
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_URUT_NIM, urutNim)
        outState.putInt(KEY_KETUK, jumlahKetuk)
    }

    // Dipanggil setelah onStart() hanya bila Activity dibuat ulang dari state.
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        urutNim = savedInstanceState.getBoolean(KEY_URUT_NIM, false)
        jumlahKetuk = savedInstanceState.getInt(KEY_KETUK, 0)
    }

    companion object {
        private const val KEY_URUT_NIM = "urut_nim"
        private const val KEY_KETUK = "jumlah_ketuk"
    }
}