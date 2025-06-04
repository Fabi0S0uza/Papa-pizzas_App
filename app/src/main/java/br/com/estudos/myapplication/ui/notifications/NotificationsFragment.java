package br.com.estudos.myapplication.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.estudos.myapplication.R;
import br.com.estudos.myapplication.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<Map<String, Object>> listaPedidos;
    private PedidoAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerPedidos;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        listaPedidos = new ArrayList<>();
        adapter = new PedidoAdapter(listaPedidos);
        recyclerView.setAdapter(adapter);

        carregarPedidos();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Se o seu PedidoAdapter tiver algum recurso que precise ser liberado, faça isso aqui.
        // Exemplo: adapter.cleanup();
    }

    private void carregarPedidos() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("compras")
                .whereEqualTo("usuarioId", user.getUid())
                .orderBy("dataCompra", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    listaPedidos.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        listaPedidos.add(doc.getData()); // Adiciona o documento como um Map
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {
        private List<Map<String, Object>> pedidos;

        public PedidoAdapter(List<Map<String, Object>> pedidos) {
            this.pedidos = pedidos;
        }

        @NonNull
        @Override
        public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
            return new PedidoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
            Map<String, Object> pedido = pedidos.get(position);

            holder.txtEmail.setText("Usuário: " + pedido.get("emailUsuario"));
            holder.txtMetodo.setText("Pagamento: " + pedido.get("metodoPagamento"));

            // Tratamento seguro para o valor total
            Object valorTotalObj = pedido.get("valorTotal");
            double valorTotal = 0.0;
            if (valorTotalObj instanceof Number) {
                valorTotal = ((Number) valorTotalObj).doubleValue();
            }
            holder.txtValor.setText(String.format("Total: R$ %.2f", valorTotal));

            // Tratamento seguro para a data
            Object dataCompraObj = pedido.get("dataCompra");
            Timestamp timestamp = null;

            if (dataCompraObj instanceof Timestamp) {
                timestamp = (Timestamp) dataCompraObj;
            } else if (dataCompraObj instanceof Map) {
                // Se for um mapa, tentamos converter para Timestamp
                Map<String, Object> timestampMap = (Map<String, Object>) dataCompraObj;
                if (timestampMap.containsKey("_seconds") && timestampMap.get("_seconds") instanceof Number) {
                    long seconds = ((Number) timestampMap.get("_seconds")).longValue();
                    // Firebase Timestamp usa segundos, então multiplicamos por 1000 para milissegundos
                    timestamp = new Timestamp(new Date(seconds * 1000));
                }
            }

            if (timestamp != null) {
                Date date = timestamp.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                holder.txtData.setText("Data: " + sdf.format(date));
            } else {
                holder.txtData.setText("Data: Indisponível");
            }

            // --- INÍCIO DA ALTERAÇÃO PARA EXIBIR OS NOMES DAS PIZZAS ---
            StringBuilder pizzasBuilder = new StringBuilder("Pizzas: ");
            Object itensObj = pedido.get("itens");

            if (itensObj instanceof List) {
                List<Map<String, Object>> itens = (List<Map<String, Object>>) itensObj;
                if (!itens.isEmpty()) {
                    for (int i = 0; i < itens.size(); i++) {
                        Map<String, Object> item = itens.get(i);
                        // O nome da pizza está no campo "nome" dentro de cada item do array "itens"
                        String nomePizza = (String) item.get("nome");
                        if (nomePizza != null && !nomePizza.isEmpty()) {
                            pizzasBuilder.append(nomePizza);
                        } else {
                            pizzasBuilder.append("Nome desconhecido");
                        }
                        if (i < itens.size() - 1) {
                            pizzasBuilder.append(", "); // Adiciona vírgula entre os nomes
                        }
                    }
                } else {
                    pizzasBuilder.append("Nenhuma pizza no pedido.");
                }
            } else {
                pizzasBuilder.append("Erro ao carregar pizzas.");
            }
            holder.txtNomePizza.setText(pizzasBuilder.toString());
            // --- FIM DA ALTERAÇÃO ---
        }

        @Override
        public int getItemCount() {
            return pedidos.size();
        }

        public class PedidoViewHolder extends RecyclerView.ViewHolder {
            TextView txtEmail, txtMetodo, txtValor, txtData, txtNomePizza;

            public PedidoViewHolder(@NonNull View itemView) {
                super(itemView);
                txtEmail = itemView.findViewById(R.id.txtEmail);
                txtMetodo = itemView.findViewById(R.id.txtMetodo);
                txtValor = itemView.findViewById(R.id.txtValor);
                txtData = itemView.findViewById(R.id.txtData);
                txtNomePizza = itemView.findViewById(R.id.txtNomePizza); // Certifique-se de que este ID existe no seu item_pedido.xml
            }
        }
    }
}