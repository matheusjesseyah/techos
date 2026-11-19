package br.com.bugnacio.techos.exception;

//Classe responsavel por criar uma exceção
//para representar erros relacionados as regras de negocio do sistema

//construtor que recebe uma mesagem explicando o motivo do erro
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem){
    //super() chama o construtor da classe pai (RunTimeException)
    //enviando a mensagem recebida
    //Assim, a exceção passa a carregar a descrição do problema

    super(mensagem);
    }

}
