package com.cavstecnologia.trabalhofinalapiscar.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cavstecnologia.trabalhofinalapiscar.R
import com.cavstecnologia.trabalhofinalapiscar.model.Car
import com.cavstecnologia.trabalhofinalapiscar.model.CarValue
import com.cavstecnologia.trabalhofinalapiscar.ui.loadUrl

class CarAdapter(
    private val cars: List<CarValue>,
    private val carClickListener: (CarValue) -> Unit
): RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarAdapter.CarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car_layout, parent, false);
        return CarViewHolder(view);
    }

    override fun onBindViewHolder(holder: CarAdapter.CarViewHolder, position: Int) {
        //Log.d("TAG", "onBindViewHolder: $position - Numero de cars na lista: ${cars.size}");
        val car : CarValue = cars[position];

        holder.name.text = car.name;
        holder.year.text = car.year;
        holder.licence.text = car.licence;
        holder.imageView.loadUrl(car.imageUrl);
        holder.itemView.setOnClickListener { carClickListener(car) };
    }

    override fun getItemCount(): Int = cars.size;

    class CarViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.image);
        val name: TextView = view.findViewById(R.id.name);
        val year: TextView = view.findViewById(R.id.year);
        val licence: TextView = view.findViewById(R.id.licence);

    }

}