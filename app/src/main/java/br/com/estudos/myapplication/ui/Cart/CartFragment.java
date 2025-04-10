package br.com.estudos.myapplication.ui.Cart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog; // Adicione esta importação para o AlertDialog

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import br.com.estudos.myapplication.Login;
import br.com.estudos.myapplication.Pizza;
import br.com.estudos.myapplication.PizzaAdapter;
import br.com.estudos.myapplication.R;
import br.com.estudos.myapplication.ui.Cart.Carrinho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment implements PizzaAdapter.OnCartUpdateListener {

    private RecyclerView recyclerView;
    private PizzaAdapter adapter;
    private TextView totalTextView;
    private Spinner spinnerPizzarias, spinnerCupons, metodoPag;
    private FirebaseFirestore db;
    private Button btn_Finalizar;

    private List<String> listaPagamentos = new ArrayList<>();
    private List<String> nomesPizzarias = new ArrayList<>();
    private Map<String, String> pizzariasMap = new HashMap<>();
    private List<String> listaCupons = new ArrayList<>();
    private Map<String, String> cuponsMap = new HashMap<>();
    private Map<String, String> cuponsDescontos = new HashMap<>(); // Novo mapa para armazenar os descontos
    private double totalCarrinho = 0.0; // Valor do carrinho antes do desconto
    private double descontoCupom = 0.0; // Valor do desconto aplicado

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewCarrinho);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        spinnerPizzarias = root.findViewById(R.id.spinnerPizzarias);
        spinnerCupons = root.findViewById(R.id.spinnerCupons);
        metodoPag = root.findViewById(R.id.metodoPag);
        btn_Finalizar = root.findViewById(R.id.finalizarCompraButton);
        listaPagamentos.add("Cartão de Crédito");
        listaPagamentos.add("Cartão de Débito");
        listaPagamentos.add("Pix");
        listaPagamentos.add("Dinheiro");

        // Cria um adaptador para o Spinner
        ArrayAdapter<String> adapterPag = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaPagamentos);
        adapterPag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metodoPag.setAdapter(adapterPag);

        totalTextView = root.findViewById(R.id.totalTextView);

        // Recupera as pizzas do carrinho
        List<Pizza> pizzasCarrinho = Carrinho.getInstance().getPizzas();
        db = FirebaseFirestore.getInstance();

        carregarPizzarias();
        selecionarPag();

        // Configura o adaptador com o listener para atualizar o total
        adapter = new PizzaAdapter(getContext(), false, true, this);
        adapter.setPizzaList(pizzasCarrinho);
        recyclerView.setAdapter(adapter);

        // Define o total inicial do carrinho
        atualizarTotal();

        // Listener para carregar cupons ao escolher uma pizzaria
        spinnerPizzarias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarCupons();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener para aplicar desconto ao selecionar um cupom
        spinnerCupons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarDescontoCupom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener do botão de finalizar
        // Listener do botão de finalizar
        btn_Finalizar.setOnClickListener(v -> {
            // Verifica se o usuário está logado e se o carrinho tem itens
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(getContext(), "Usuário não autenticado! Redirecionando para login...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return;
            }

            if (Carrinho.getInstance().getPizzas().isEmpty()) {
                Toast.makeText(getContext(), "Erro: O carrinho está vazio!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Exibe o pedido de confirmação antes de finalizar a compra
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmar Compra")
                    .setMessage("Você tem certeza que deseja finalizar o pedido?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            salvarCompra(); // Salva a compra se o usuário confirmar
                        }
                    })
                    .setNegativeButton("Cancelar", null) // Não faz nada se o usuário cancelar
                    .show();
        });


        return root;
    }

    @Override
    public void onCartUpdated() {
        atualizarTotal();
    }

    public void atualizarTotal() {
        totalCarrinho = Carrinho.getInstance().calcularCarrinho();
        double totalComDesconto = totalCarrinho - descontoCupom;
        if (totalComDesconto < 0) totalComDesconto = 0; // Evita valores negativos

        if (descontoCupom > 0) {
            totalTextView.setText(String.format("Total: R$ %.2f (-R$ %.2f de desconto)", totalComDesconto, descontoCupom));
        } else {
            totalTextView.setText(String.format("Total: R$ %.2f", totalComDesconto));
        }
    }

    private void carregarPizzarias() {
        db.collection("pizzarias").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                nomesPizzarias.clear();
                pizzariasMap.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String nome = document.getString("nome");
                    String id = document.getId();
                    pizzariasMap.put(id, nome);
                    nomesPizzarias.add(nome);
                }
                atualizarSpinnerPizzarias();
            }
        });
    }

    private void atualizarSpinnerPizzarias() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nomesPizzarias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPizzarias.setAdapter(adapter);
    }

    private void carregarCupons() {
        String pizzariaSelecionada = (String) spinnerPizzarias.getSelectedItem();
        String pizzariaId = getKeyByValue(pizzariasMap, pizzariaSelecionada);

        if (pizzariaId == null) return;

        db.collection("pizzarias").document(pizzariaId).collection("cupons").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaCupons.clear();
                        cuponsMap.clear();
                        cuponsDescontos.clear();

                        // Adiciona a opção "Sem desconto"
                        listaCupons.add("Sem desconto");
                        cuponsDescontos.put("Sem desconto", "0"); // Define desconto 0 para essa opção

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String codigo = document.getString("codigo");
                            String desconto = document.getString("desconto");
                            String id = document.getId();

                            cuponsMap.put(id, codigo);
                            listaCupons.add(codigo);
                            cuponsDescontos.put(codigo, desconto);
                        }
                        atualizarSpinnerCupons();
                    }
                });
    }

    private void atualizarSpinnerCupons() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaCupons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCupons.setAdapter(adapter);
    }

    private void aplicarDescontoCupom() {
        String cupomSelecionado = (String) spinnerCupons.getSelectedItem();
        if (cupomSelecionado == null) return;

        if (cupomSelecionado.equals("Sem desconto")) {
            descontoCupom = 0; // Remove o desconto
        } else {
            String descontoStr = cuponsDescontos.get(cupomSelecionado);
            if (descontoStr != null) {
                try {
                    descontoCupom = Double.parseDouble(descontoStr);
                } catch (NumberFormatException e) {
                    descontoCupom = 0;
                }
            } else {
                descontoCupom = 0;
            }
        }

        atualizarTotal();
    }

    private void selecionarPag() {
        String meiodepagamento = (String) metodoPag.getSelectedItem();
    }

    private String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void salvarCompra() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "Erro: Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String emailUsuario = user.getEmail();
        String metodoPagamento = (String) metodoPag.getSelectedItem();

        double valorFinal = 0;
        List<Map<String, Object>> itensComprados = new ArrayList<>();

        for (Pizza pizza : Carrinho.getInstance().getPizzas()) {
            Map<String, Object> item = new HashMap<>();
            item.put("nome", pizza.getNome());
            item.put("preco", pizza.getPreco());
            item.put("imagemUrl", pizza.getImagemUrl());
            itensComprados.add(item);
            valorFinal += pizza.getPreco();
        }

        Map<String, Object> compra = new HashMap<>();
        compra.put("usuarioId", userId);
        compra.put("emailUsuario", emailUsuario);
        compra.put("metodoPagamento", metodoPagamento);
        compra.put("valorTotal", valorFinal);
        compra.put("dataCompra", FieldValue.serverTimestamp());
        compra.put("itens", itensComprados);

        db.collection("compras")
                .add(compra)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Compra registrada com sucesso!", Toast.LENGTH_SHORT).show();
                    Carrinho.getInstance().esvaziarCarrinho(); // Esvazia o carrinho
                    adapter.setPizzaList(new ArrayList<>()); // Atualiza o RecyclerView
                    adapter.notifyDataSetChanged(); // Notifica mudança na UI
                    atualizarTotal(); // Atualiza o total para R$ 0,00
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao registrar compra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
