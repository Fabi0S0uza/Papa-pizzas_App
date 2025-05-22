package br.com.estudos.myapplication;

public class Cupom {
        private String codigo;
        private String desconto;

        public Cupom() {
            // Construtor vazio necessário para Firestore
        }

        public Cupom(String codigo, String desconto) {
            this.codigo = codigo;
            this.desconto = desconto;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getDesconto() {
            return desconto;
        }
    }



