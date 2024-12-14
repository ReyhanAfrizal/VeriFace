import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.reyhan.veriface.R
import com.reyhan.veriface.model.ResponseVeriface

class VerifaceAdapter(
    private val dataList: List<ResponseVeriface?>,
    private val onItemClick: (ResponseVeriface) -> Unit
) : RecyclerView.Adapter<VerifaceAdapter.VerifaceViewHolder>() {

    inner class VerifaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtNamaKonsumen: TextView = itemView.findViewById(R.id.txtNamaItem)
        var txtTanggal: TextView = itemView.findViewById(R.id.txtWaktuItem)
        var imgKtp: ImageView = itemView.findViewById(R.id.imgKtp)
        var imgSelfie: ImageView = itemView.findViewById(R.id.imgSelfie)
        var txtResult: TextView = itemView.findViewById(R.id.txtResult)
        var txtNotes: TextView = itemView.findViewById(R.id.txtNotesItem)

        init {
            // Handle item click
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    dataList[position]?.let { data -> onItemClick(data) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerifaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return VerifaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerifaceViewHolder, position: Int) {
        val data = dataList[position]

        holder.txtNamaKonsumen.text = data?.namaKonsumen ?: "Nama Konsumen Tidak Tersedia"
        holder.txtTanggal.text = formatDateTime(data?.createdDataAt)
        holder.txtNotes.text = data?.notes ?: "Notes Tidak Tersedia"

        // Load images using Picasso
        val ktpImageUrl = data?.fotoKtp ?: ""
        val selfieImageUrl = data?.fotoSelfie ?: ""

        if (ktpImageUrl.isNotEmpty()) {
            Picasso.get().load(ktpImageUrl).into(holder.imgKtp)
        } else {
            //holder.imgKtp.setImageResource(R.drawable.default_ktp_image)  // Placeholder or default image
        }

        if (selfieImageUrl.isNotEmpty()) {
            Picasso.get().load(selfieImageUrl).into(holder.imgSelfie)
        } else {
            //holder.imgSelfie.setImageResource(R.drawable.default_selfie_image)  // Placeholder or default image
        }

        holder.txtResult.text = data?.result ?: "No result available"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private fun formatDateTime(dateTimeString: String?): String {
        // Here you can format the date-time string into a desired format (if needed)
        return dateTimeString ?: "Date not available"
    }
}
