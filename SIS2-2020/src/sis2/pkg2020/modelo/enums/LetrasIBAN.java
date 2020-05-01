/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sis2.pkg2020.modelo.enums;

/**
 * Enum que contiene los valores respectivos de los diferentes paises
 * @author Marco Speranza López
 */
public enum LetrasIBAN {
    A(10), G(16),M(22), S(28),Y(34),
B(11) ,H(17) ,N(23),T(29),Z(35),
C(12), I(18), O(24), U(30),
D(13) ,J(19) ,P(25) ,V(31),
E(14), K(20),Q(26), W(32),
F(15) ,L(21) ,R(27),X(33);

private int peso;
    private LetrasIBAN(int peso){

    this.peso = peso;
    
    }
    
    public int getPeso(){
        
        return peso;
    }
}
