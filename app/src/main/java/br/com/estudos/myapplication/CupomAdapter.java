package br.com.estudos.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CupomAdapter extends RecyclerView.Adapter<CupomAdapter.ViewHolder> {
    private List<Cupom> cupomList;

    public CupomAdapter(List<Cupom> cupomList) {
        this.cupomList = cupomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cupom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cupom cupom = cupomList.get(position);
        holder.codigo.setText("Código: " + cupom.getCodigo());
        holder.desconto.setText("Desconto: " + cupom.getDesconto() + "R$ OFF");
    }

    @Override
    public int getItemCount() {
        return cupomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView codigo, desconto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            codigo = itemView.findViewById(R.id.cupom_name);
            desconto = itemView.findViewById(R.id.cupom_discount);
        }
    }
}
