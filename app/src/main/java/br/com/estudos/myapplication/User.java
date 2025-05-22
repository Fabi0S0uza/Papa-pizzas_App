package br.com.estudos.myapplication;

public class User {
    private String name;
    private String cpf;
    private String phone;
    private String email;

    // Construtor
    public User(String name, String cpf, String phone, String email) {
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.email = email;
    }

    // Getters e setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
