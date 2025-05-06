package ph.edu.usc.ise;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
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

    public interface OnItemClickListener {
        void onItemClick(HealthDocument document);
    }

    // Constructor for the adapter
    public DocumentAdapter(List<HealthDocument> docs, Context context, OnItemClickListener listener) {
        this.docs = docs;
        this.context = context;
        this.listener = listener;
    }

    // ViewHolder class to handle individual document items
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, date;

        public ViewHolder(View view, OnItemClickListener listener) {
            super(view);
            thumbnail = view.findViewById(R.id.imgThumbnail);
            title = view.findViewById(R.id.txtTitle);
            date = view.findViewById(R.id.txtDate);

            // Handle item clicks
            view.setOnClickListener(v -> {
                if (listener != null) {
                    HealthDocument clickedDoc = (HealthDocument) v.getTag(); // Get the document from the view's tag
                    listener.onItemClick(clickedDoc); // Pass the clicked document to the listener
                }
            });
        }
    }

    // Update the list of documents in the adapter
    public void updateList(List<HealthDocument> filteredDocuments) {
        Log.d("RecyclerView", "Updating adapter with " + filteredDocuments.size() + " documents.");
        this.docs = filteredDocuments;
        notifyDataSetChanged();
    }

    // Create a new ViewHolder when RecyclerView needs a new one
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new ViewHolder(view, listener);
    }

    // Bind data to the ViewHolder for each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HealthDocument doc = docs.get(position); // Get the document at the current position
        holder.title.setText(doc.title);
        holder.date.setText(doc.date);

        // Set thumbnail based on file type
        if (doc.filePath.endsWith(".pdf")) {
            holder.thumbnail.setImageResource(R.drawable.ic_pdf); // PDF icon
        } else {
            holder.thumbnail.setImageURI(Uri.parse(doc.filePath)); // Set image URI for other file types
        }

        // Tag the view with the document so we can access it during the click
        holder.itemView.setTag(doc);
    }

    // Return the number of items in the list
    @Override
    public int getItemCount() {
        return docs.size();
    }
}
