package br.com.estudos.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private EditText edtNomePizzaria, edtCodigoCupom, edtDescontoCupom,  edtValidadeCupom;
    private Button btnAdicionarPizzaria, btnAdicionarCupom, btnDeletarPizzaria, btnDeletarCupom;
    private Spinner spinnerPizzarias, spinnerCupons;
    private FirebaseFirestore db;
    private List<String> nomesPizzarias = new ArrayList<>();
    private Map<String, String> pizzariasMap = new HashMap<>();
    private List<String> listaCupons = new ArrayList<>();
    private Map<String, String> cuponsMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        edtNomePizzaria = findViewById(R.id.edtNomePizzaria);
        edtCodigoCupom = findViewById(R.id.edtCodigoCupom);
        edtDescontoCupom = findViewById(R.id.edtDescontoCupom);
        btnAdicionarPizzaria = findViewById(R.id.btnAdicionarPizzaria);
        btnAdicionarCupom = findViewById(R.id.btnAdicionarCupom);
        btnDeletarPizzaria = findViewById(R.id.btnDeletarPizzaria);
        btnDeletarCupom = findViewById(R.id.btnDeletarCupom);
        spinnerPizzarias = findViewById(R.id.spinnerPizzarias);
        spinnerCupons = findViewById(R.id.spinnerCupons);
        edtValidadeCupom = findViewById(R.id.edtValidadeCupom);

        db = FirebaseFirestore.getInstance();

        carregarPizzarias();

        btnAdicionarPizzaria.setOnClickListener(v -> adicionarPizzaria());
        btnAdicionarCupom.setOnClickListener(v -> adicionarCupom());
        btnDeletarPizzaria.setOnClickListener(v -> deletarPizzaria());
        btnDeletarCupom.setOnClickListener(v -> deletarCupom());

        // 🚀 Adicionado: Listener para atualizar os cupons quando uma pizzaria for selecionada
        spinnerPizzarias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarCupons();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada a fazer aqui
            }
        });
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomesPizzarias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPizzarias.setAdapter(adapter);
    }

    private void carregarCupons() {
        if (spinnerPizzarias.getSelectedItem() == null) return;

        String pizzariaSelecionada = spinnerPizzarias.getSelectedItem().toString();
        String pizzariaId = getKeyByValue(pizzariasMap, pizzariaSelecionada);

        if (pizzariaId == null) return;

        db.collection("pizzarias").document(pizzariaId).collection("cupons").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaCupons.clear();
                        cuponsMap.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String codigo = document.getString("codigo");
                            String id = document.getId();
                            cuponsMap.put(id, codigo);
                            listaCupons.add(codigo);
                        }
                        atualizarSpinnerCupons();
                    }
                });
    }

    private void atualizarSpinnerCupons() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCupons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCupons.setAdapter(adapter);
    }

    private void adicionarPizzaria() {
        String nome = edtNomePizzaria.getText().toString().trim();
        if (nome.isEmpty()) {
            Toast.makeText(this, "Digite um nome para a pizzaria", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pizzaria = new HashMap<>();
        pizzaria.put("nome", nome);

        db.collection("pizzarias").add(pizzaria).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Pizzaria adicionada!", Toast.LENGTH_SHORT).show();
            edtNomePizzaria.setText("");
            carregarPizzarias();
        });
    }

    private void deletarPizzaria() {
        String pizzariaSelecionada = spinnerPizzarias.getSelectedItem().toString();
        String pizzariaId = getKeyByValue(pizzariasMap, pizzariaSelecionada);

        if (pizzariaId == null) {
            Toast.makeText(this, "Selecione uma pizzaria para deletar!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("pizzarias").document(pizzariaId).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Pizzaria deletada!", Toast.LENGTH_SHORT).show();
            carregarPizzarias();
        });
    }

    private void adicionarCupom() {
        String codigo = edtCodigoCupom.getText().toString().trim();
        String desconto = edtDescontoCupom.getText().toString().trim();
        String validadeTexto = edtValidadeCupom.getText().toString().trim();

        if (codigo.isEmpty() || desconto.isEmpty() || validadeTexto.isEmpty() || spinnerPizzarias.getSelectedItem() == null) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        int diasValidade;
        try {
            diasValidade = Integer.parseInt(validadeTexto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Insira um número válido de dias!", Toast.LENGTH_SHORT).show();
            return;
        }

        String pizzariaNome = spinnerPizzarias.getSelectedItem().toString();
        String pizzariaId = getKeyByValue(pizzariasMap, pizzariaNome);

        Map<String, Object> dadosCupom = new HashMap<>();
        dadosCupom.put("codigo", codigo);
        dadosCupom.put("desconto", desconto);
        dadosCupom.put("validade", new Date(System.currentTimeMillis() + (diasValidade * 24L * 60 * 60 * 1000)));

        db.collection("pizzarias").document(pizzariaId)
                .collection("cupons")
                .add(dadosCupom)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Cupom adicionado com validade de " + diasValidade + " dias!", Toast.LENGTH_SHORT).show();
                    edtCodigoCupom.setText("");
                    edtDescontoCupom.setText("");
                    edtValidadeCupom.setText("");
                    carregarCupons();
                });
    }


    private void deletarCupom() {
        String cupomSelecionado = spinnerCupons.getSelectedItem().toString();
        String cupomId = getKeyByValue(cuponsMap, cupomSelecionado);
        String pizzariaSelecionada = spinnerPizzarias.getSelectedItem().toString();
        String pizzariaId = getKeyByValue(pizzariasMap, pizzariaSelecionada);

        if (pizzariaId == null || cupomId == null) {
            Toast.makeText(this, "Selecione um cupom para deletar!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("pizzarias").document(pizzariaId).collection("cupons").document(cupomId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cupom deletado!", Toast.LENGTH_SHORT).show();
                    carregarCupons();
                });
    }

    private String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
