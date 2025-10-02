package com.example.smartrecycle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrecycle.R;
import com.example.smartrecycle.model.WasteResult;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WasteHistoryAdapter extends RecyclerView.Adapter<WasteHistoryAdapter.WasteHistoryViewHolder> {

    private List<WasteResult> wasteResults;
    private Context context;
    private OnWasteResultClickListener listener;
    private OnWasteResultLongClickListener longClickListener;

    public interface OnWasteResultClickListener {
        void onWasteResultClick(WasteResult wasteResult);
    }

    public interface OnWasteResultLongClickListener {
        void onWasteResultLongClick(WasteResult wasteResult, int position);
    }

    public WasteHistoryAdapter(Context context, List<WasteResult> wasteResults, OnWasteResultClickListener listener) {
        this.context = context;
        this.wasteResults = wasteResults;
        this.listener = listener;
    }

    public void setOnWasteResultLongClickListener(OnWasteResultLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public WasteHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waste_history, parent, false);
        return new WasteHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WasteHistoryViewHolder holder, int position) {
        WasteResult wasteResult = wasteResults.get(position);
        holder.bind(wasteResult);
    }

    @Override
    public int getItemCount() {
        return wasteResults != null ? wasteResults.size() : 0;
    }

    public void updateData(List<WasteResult> newWasteResults) {
        this.wasteResults = newWasteResults;
        notifyDataSetChanged();
    }

    class WasteHistoryViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView wasteIcon;
        private TextView wasteType;
        private TextView wasteCategory;
        private TextView wasteDate;
        private TextView wastePoints;
        private TextView timeAgo;

        public WasteHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            wasteIcon = itemView.findViewById(R.id.wasteIcon);
            wasteType = itemView.findViewById(R.id.wasteType);
            wasteCategory = itemView.findViewById(R.id.wasteCategory);
            wasteDate = itemView.findViewById(R.id.wasteDate);
            wastePoints = itemView.findViewById(R.id.wastePoints);
            timeAgo = itemView.findViewById(R.id.timeAgo);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onWasteResultClick(wasteResults.get(position));
                }
            });

            cardView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && longClickListener != null) {
                    longClickListener.onWasteResultLongClick(wasteResults.get(position), position);
                    return true;
                }
                return false;
            });
        }

        public void bind(WasteResult wasteResult) {
            // Type de déchet
            wasteType.setText(wasteResult.getWasteType());
            
            // Catégorie
            if (wasteResult.getWasteCategory() != null) {
                wasteCategory.setText(wasteResult.getWasteCategory());
            } else {
                wasteCategory.setText("Non classé");
            }
            
            // Points
            if (wasteResult.getWastePoints() != null) {
                wastePoints.setText(String.valueOf(wasteResult.getWastePoints()) + " pts");
            } else {
                wastePoints.setText("0 pts");
            }
            
            // Date
            if (wasteResult.getWasteDate() != null) {
                try {
                    // Essayer de parser la date si c'est une String
                    String dateString = wasteResult.getWasteDate();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    
                    java.util.Date date = inputFormat.parse(dateString);
                    wasteDate.setText(outputFormat.format(date));
                } catch (Exception e) {
                    // Si le parsing échoue, afficher la date brute ou un message par défaut
                    wasteDate.setText(wasteResult.getWasteDate());
                }
            } else {
                wasteDate.setText("Date inconnue");
            }
            
            // Time ago
            if (wasteResult.getTimeAgo() != null) {
                timeAgo.setText(wasteResult.getTimeAgo());
            } else {
                timeAgo.setText("Récemment");
            }
            
            // Icône (placeholder pour l'instant)
            // TODO: Charger l'icône depuis wasteResult.getWasteIcon()
            setWasteIcon(wasteResult.getWasteType());
        }

        private void setWasteIcon(String wasteType) {
            int iconResId;
            try {
                switch (wasteType != null ? wasteType.toLowerCase() : "") {
                    case "plastic":
                        iconResId = R.drawable.ic_plastic;
                        break;
                    case "paper":
                        iconResId = R.drawable.ic_paper;
                        break;
                    case "glass":
                        iconResId = R.drawable.ic_glass;
                        break;
                    case "metal":
                        iconResId = R.drawable.ic_metal;
                        break;
                    case "organic":
                        iconResId = R.drawable.ic_organic;
                        break;
                    default:
                        iconResId = R.drawable.logo_recylitix;
                        break;
                }
                wasteIcon.setImageResource(iconResId);
            } catch (Exception e) {
                // En cas d'erreur, utiliser l'icône par défaut
                wasteIcon.setImageResource(R.drawable.logo_recylitix);
            }
        }
    }
}
