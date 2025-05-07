package ph.edu.usc.ise;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    private List<HealthDocument> docs;
    private Context context;
    private OnItemClickListener listener;

    // Interface for click callback
    public interface OnItemClickListener {
        void onItemClick(HealthDocument document);
    }

    public DocumentAdapter(List<HealthDocument> docs, Context context, OnItemClickListener listener) {
        this.docs = docs;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, date;

        public ViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.imgThumbnail);
            title = view.findViewById(R.id.txtTitle);
            date = view.findViewById(R.id.txtDate);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HealthDocument doc = docs.get(position);
        holder.title.setText(doc.title);
        holder.date.setText(doc.date);

        if (doc.filePath.endsWith(".pdf")) {
            holder.thumbnail.setImageResource(R.drawable.ic_pdf);
        } else {
            holder.thumbnail.setImageURI(Uri.parse(doc.filePath));
        }

        // Set click listener to go to DocumentDetailActivity
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(doc);
            }
        });
    }

    @Override
    public int getItemCount() {
        return docs.size();
    }

    public void updateList(List<HealthDocument> filteredDocuments) {
        this.docs = filteredDocuments;
        notifyDataSetChanged();
    }
}
