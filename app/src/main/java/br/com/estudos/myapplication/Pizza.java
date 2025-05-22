package br.com.estudos.myapplication;

public class Pizza {
    private String nome;
    private double preco;
    private String imagemUrl;

    public Pizza() {
    }

    public Pizza(String nome, double preco, String imagemUrl) {
        this.nome = nome;
        this.preco = preco;
        this.imagemUrl = imagemUrl;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
}

