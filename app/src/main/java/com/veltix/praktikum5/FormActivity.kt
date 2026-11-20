package com.veltix.praktikum5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class FormActivity : LoggedActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etNim: EditText
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etJurusan: EditText
    private lateinit var etNomorHp: EditText
    private lateinit var btnSimpan: Button

    // null = mode tambah; berisi id = mode ubah.
    private var editId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        db = DatabaseHelper(this)
        etNim = findViewById(R.id.etNim)
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etJurusan = findViewById(R.id.etJurusan)
        etNomorHp = findViewById(R.id.etNomorHp)
        btnSimpan = findViewById(R.id.btnSimpan)

        // Tanpa extra id (atau id -1) berarti menambah data baru.
        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id > 0) db.getMahasiswaById(id)?.let { isiForm(it) }

        btnSimpan.setOnClickListener { simpan() }
        findViewById<Button>(R.id.btnBatal).setOnClickListener { finish() }
    }

    private fun isiForm(mhs: Mahasiswa) {
        editId = mhs.id
        etNim.setText(mhs.nim)
        etNama.setText(mhs.nama)
        etAlamat.setText(mhs.alamat)
        etJurusan.setText(mhs.jurusan)
        etNomorHp.setText(mhs.nomorHp)
        btnSimpan.text = "Perbarui"
        findViewById<TextView>(R.id.tvJudulForm).text = "Ubah Data"
    }

    private fun simpan() {
        val nim = etNim.text.toString().trim()
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val jurusan = etJurusan.text.toString().trim()
        val nomorHp = etNomorHp.text.toString().trim()

        if (!validasi(nim, nama, nomorHp)) return

        val id = editId
        val mhs = Mahasiswa(
            id = id ?: 0, nim = nim, nama = nama,
            alamat = alamat, jurusan = jurusan, nomorHp = nomorHp
        )

        val berhasil = if (id == null) {
            db.insertMahasiswa(mhs) != -1L
        } else {
            db.updateMahasiswa(mhs) > 0
        }

        if (berhasil) {
            val aksi = if (id == null) "ditambahkan" else "diperbarui"
            Log.i("Data", "Data $aksi: nim=$nim")
            Toast.makeText(this, "Data berhasil $aksi", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Log.e("Data", "Gagal menyimpan nim=$nim")
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validasi(nim: String, nama: String, nomorHp: String): Boolean {
        var valid = true

        if (nim.isEmpty()) {
            etNim.error = "NIM wajib diisi"
            valid = false
        } else if (nim.length < 5 || !nim.all { it.isDigit() }) {
            etNim.error = "NIM minimal 5 digit angka"
            valid = false
        } else if (db.isNimTerdaftar(nim, editId ?: 0)) {
            Log.w("Data", "NIM $nim sudah terdaftar")
            etNim.error = "NIM sudah terdaftar"
            valid = false
        }

        if (nama.isEmpty()) {
            etNama.error = "Nama wajib diisi"
            valid = false
        }

        if (nomorHp.isNotEmpty() && !hpValid(nomorHp)) {
            etNomorHp.error = "Nomor HP tidak valid (contoh 081234567890)"
            valid = false
        }

        return valid
    }

    private fun hpValid(hp: String): Boolean {
        return hp.startsWith("08") && hp.length in 10..13 && hp.all { it.isDigit() }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}