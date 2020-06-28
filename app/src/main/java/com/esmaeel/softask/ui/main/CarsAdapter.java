package com.esmaeel.softask.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.esmaeel.softask.Utils.Utils;
import com.esmaeel.softask.data.Models.CarsResponseModel;
import com.esmaeel.softask.databinding.CarAdapterItemBinding;

import java.util.ArrayList;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.CarHolder> {
    private ArrayList<CarsResponseModel.DataBean> carsList = new ArrayList<>();

    @NonNull
    @Override
    public CarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CarHolder(CarAdapterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CarHolder holder, int position) {
        holder.bindViews(carsList.get(position));
    }

    @Override
    public int getItemCount() {
        return carsList == null ? 0 : carsList.size();
    }


    public void updateCars(ArrayList<CarsResponseModel.DataBean> newList) {
        int start = getItemCount();
        if (Utils.isNotEmptyOrNull(newList)){
            this.carsList.addAll(newList);
            if (start > 0) {
                notifyItemRangeInserted(start, carsList.size() - 1);
            } else {
                notifyDataSetChanged();
            }
        }

//        if (newList != null) {
//            if (!newList.isEmpty()) {
//                this.carsList.addAll(newList);
//                if (start > 0) {
//                    notifyItemRangeInserted(start, carsList.size() - 1);
//                } else {
//                    notifyDataSetChanged();
//                }
//            }
//        }
    }

    static class CarHolder extends RecyclerView.ViewHolder {
        private CarAdapterItemBinding binder;

        public CarHolder(@NonNull CarAdapterItemBinding binder) {
            super(binder.getRoot());
            this.binder = binder;
        }

        void bindViews(CarsResponseModel.DataBean model) {
            binder.brand.setText(model.getBrand());
            binder.carStatus.setText(model.getUsedStatus());
            binder.year.setText(model.getConstructionYear());
            Glide.with(binder.carImage).load(model.getImageUrl()).into(binder.carImage);
        }
    }

}
