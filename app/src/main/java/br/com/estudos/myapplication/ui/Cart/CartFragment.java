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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import br.com.estudos.myapplication.Login;
import br.com.estudos.myapplication.Pizza;
import br.com.estudos.myapplication.PizzaAdapter;
import br.com.estudos.myapplication.R;
import br.com.estudos.myapplication.databinding.FragmentCartBinding;
import br.com.estudos.myapplication.ui.Cart.Carrinho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment implements PizzaAdapter.OnCartUpdateListener {
    private FragmentCartBinding binding;
    private RecyclerView recyclerView;
    private PizzaAdapter adapter;
    private TextView totalTextView;
    private Spinner spinnerPizzarias, spinnerCupons, metodoPag;
    private EditText editTextEndereco;
    private FirebaseFirestore db;
    private Button btn_Finalizar;

    private ImageView emptyCartImageView; // Nova variável para a imagem de carrinho vazio
    private TextView emptyCartTextView;   // Nova variável para o texto de carrinho vazio
    private TextView cartItemsTitle;      // Variável para o título "Itens no Carrinho"

    // Seus outros TextViews de título e os Spinners e EditText
    private TextView textView2Pizzaria;
    private TextView textView3Cupons;
    private TextView textViewEndereco;
    private TextView textView4Pagamento;


    private List<String> listaPagamentos = new ArrayList<>();
    private List<String> nomesPizzarias = new ArrayList<>();
    private Map<String, String> pizzariasMap = new HashMap<>();
    private List<String> listaCupons = new ArrayList<>();
    private Map<String, String> cuponsMap = new HashMap<>();
    private Map<String, String> cuponsDescontos = new HashMap<>();
    private double totalCarrinho = 0.0;
    private double descontoCupom = 0.0;
    private String cupomUsadoCodigo = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerViewCarrinho;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicialize os novos elementos de UI
        emptyCartImageView = binding.emptyCartImageView;
        emptyCartTextView = binding.emptyCartTextView;
        cartItemsTitle = binding.cartItemsTitle; // Inicialize o título "Itens no Carrinho"

        // Inicialize os Spinners, EditText e Button Finalizar
        spinnerPizzarias = binding.spinnerPizzarias;
        spinnerCupons = binding.spinnerCupons;
        metodoPag = binding.metodoPag;
        editTextEndereco = binding.editTextEndereco;
        btn_Finalizar = binding.finalizarCompraButton;
        totalTextView = binding.totalTextView;

        // Inicialize os outros TextViews de título
        textView2Pizzaria = binding.textView2;
        textView3Cupons = binding.textView3;
        textViewEndereco = binding.textViewEndereco;
        textView4Pagamento = binding.textView4;


        listaPagamentos.add("Cartão de Crédito");
        listaPagamentos.add("Cartão de Débito");
        listaPagamentos.add("Pix");
        listaPagamentos.add("Dinheiro");

        ArrayAdapter<String> adapterPag = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaPagamentos);
        adapterPag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metodoPag.setAdapter(adapterPag);

        List<Pizza> pizzasCarrinho = Carrinho.getInstance().getPizzas();
        db = FirebaseFirestore.getInstance();

        carregarPizzarias();
        selecionarPag();

        adapter = new PizzaAdapter(getContext(), false, true, this);
        adapter.setPizzaList(pizzasCarrinho);
        recyclerView.setAdapter(adapter);

        // Chame updateCartVisibility() uma vez no início para definir o estado inicial
        updateCartVisibility();

        atualizarTotal(); // Define o total inicial do carrinho

        spinnerPizzarias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarCupons();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCupons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarDescontoCupom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btn_Finalizar.setOnClickListener(v -> {
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

            String endereco = editTextEndereco.getText().toString().trim();
            if (endereco.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, informe seu endereço de entrega!", Toast.LENGTH_SHORT).show();
                editTextEndereco.requestFocus();
                return;
            }

            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmar Compra")
                    .setMessage("Você tem certeza que deseja finalizar o pedido?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            salvarCompra();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

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
    }

    @Override
    public void onCartUpdated() {
        atualizarTotal();
        updateCartVisibility(); // Chame aqui para atualizar a visibilidade
    }

    public void atualizarTotal() {
        totalCarrinho = Carrinho.getInstance().calcularCarrinho();
        double totalComDesconto = totalCarrinho - descontoCupom;
        if (totalComDesconto < 0) totalComDesconto = 0;

        if (descontoCupom > 0) {
            totalTextView.setText(String.format("Total: R$ %.2f (R$ %.2f de desconto)", totalComDesconto, descontoCupom));
        } else {
            totalTextView.setText(String.format("Total: R$ %.2f", totalComDesconto));
        }
    }

    // --- NOVO MÉTODO PARA GERENCIAR A VISIBILIDADE ---
    private void updateCartVisibility() {
        boolean isCartEmpty = Carrinho.getInstance().getPizzas().isEmpty();

        if (isCartEmpty) {
            // Oculta o RecyclerView e todos os controles de compra
            recyclerView.setVisibility(View.GONE);
            cartItemsTitle.setVisibility(View.GONE); // Oculta o título "Itens no Carrinho"
            spinnerPizzarias.setVisibility(View.GONE);
            textView2Pizzaria.setVisibility(View.GONE);
            spinnerCupons.setVisibility(View.GONE);
            textView3Cupons.setVisibility(View.GONE);
            totalTextView.setVisibility(View.GONE);
            editTextEndereco.setVisibility(View.GONE);
            textViewEndereco.setVisibility(View.GONE);
            metodoPag.setVisibility(View.GONE);
            textView4Pagamento.setVisibility(View.GONE);
            btn_Finalizar.setVisibility(View.GONE);

            // Mostra a imagem e o texto de carrinho vazio
            emptyCartImageView.setVisibility(View.VISIBLE);
            emptyCartTextView.setVisibility(View.VISIBLE);
        } else {
            // Mostra o RecyclerView e todos os controles de compra
            recyclerView.setVisibility(View.VISIBLE);
            cartItemsTitle.setVisibility(View.VISIBLE); // Mostra o título "Itens no Carrinho"
            spinnerPizzarias.setVisibility(View.VISIBLE);
            textView2Pizzaria.setVisibility(View.VISIBLE);
            spinnerCupons.setVisibility(View.VISIBLE);
            textView3Cupons.setVisibility(View.VISIBLE);
            totalTextView.setVisibility(View.VISIBLE);
            editTextEndereco.setVisibility(View.VISIBLE);
            textViewEndereco.setVisibility(View.VISIBLE);
            metodoPag.setVisibility(View.VISIBLE);
            textView4Pagamento.setVisibility(View.VISIBLE);
            btn_Finalizar.setVisibility(View.VISIBLE);

            // Oculta a imagem e o texto de carrinho vazio
            emptyCartImageView.setVisibility(View.GONE);
            emptyCartTextView.setVisibility(View.GONE);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String userId = user.getUid();

        db.collection("pizzarias").document(pizzariaId).collection("cupons").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaCupons.clear();
                        cuponsMap.clear();
                        cuponsDescontos.clear();

                        listaCupons.add("Nenhum Cupom Selecionado");
                        cuponsDescontos.put("Nenhum Cupom Selecionado", "0");

                        db.collection("usuarios").document(userId).collection("cuponsUsados").get().addOnCompleteListener(usedTask -> {
                            List<String> usedCouponCodes = new ArrayList<>();
                            if (usedTask.isSuccessful()) {
                                for (QueryDocumentSnapshot usedDoc : usedTask.getResult()) {
                                    usedCouponCodes.add(usedDoc.getId());
                                }
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String codigo = document.getString("codigo");
                                String desconto = document.getString("desconto");
                                String id = document.getId();

                                if (!usedCouponCodes.contains(codigo)) {
                                    cuponsMap.put(id, codigo);
                                    listaCupons.add(codigo);
                                    cuponsDescontos.put(codigo, desconto);
                                }
                            }
                            atualizarSpinnerCupons();
                        });
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

        if (cupomSelecionado.equals("Nenhum Cupom Selecionado") || cupomSelecionado.equals("Nenhum Cupom Selecionado")) { // Adicionado "Sem desconto"
            descontoCupom = 0;
            cupomUsadoCodigo = null;
        } else {
            cupomUsadoCodigo = cupomSelecionado;
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
        String endereco = editTextEndereco.getText().toString().trim();

        double valorFinal = totalCarrinho - descontoCupom; // Use o total com desconto
        if (valorFinal < 0) valorFinal = 0; // Garante que não seja negativo

        List<Map<String, Object>> itensComprados = new ArrayList<>();

        for (Pizza pizza : Carrinho.getInstance().getPizzas()) {
            Map<String, Object> item = new HashMap<>();
            item.put("nome", pizza.getNome());
            item.put("preco", pizza.getPreco());
            item.put("imagemUrl", pizza.getImagemUrl());
            itensComprados.add(item);
        }

        Map<String, Object> compra = new HashMap<>();
        compra.put("usuarioId", userId);
        compra.put("emailUsuario", emailUsuario);
        compra.put("metodoPagamento", metodoPagamento);
        compra.put("endereco", endereco);
        compra.put("valorTotal", valorFinal); // Agora usando o valor final com desconto
        compra.put("dataCompra", FieldValue.serverTimestamp());
        compra.put("itens", itensComprados);

        if (cupomUsadoCodigo != null && !cupomUsadoCodigo.equals("Sem desconto")) { // Verifica se um cupom foi realmente usado
            compra.put("cupomUsado", cupomUsadoCodigo);
            db.collection("usuarios").document(userId).collection("cuponsUsados").document(cupomUsadoCodigo)
                    .set(new HashMap<>())
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao registrar cupom usado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        db.collection("compras")
                .add(compra)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Compra registrada com sucesso!", Toast.LENGTH_SHORT).show();
                    Carrinho.getInstance().esvaziarCarrinho();
                    adapter.setPizzaList(new ArrayList<>());
                    adapter.notifyDataSetChanged();
                    atualizarTotal();
                    editTextEndereco.setText("");
                    cupomUsadoCodigo = null;
                    carregarCupons();
                    updateCartVisibility(); // Chamada importante aqui
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao registrar compra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}