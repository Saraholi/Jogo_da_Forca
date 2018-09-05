package br.com.jandira.senai.jogodaforca;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class JogoActivity extends AppCompatActivity {

    TextView txtPalavraJogo;
    TextView txtErros;
    TextView txtAcertos;
    TextView txtCategoria;
    String palavra = "";
    MediaPlayer musica;
    ImageView imgForca;
    char letraEscolhida;
    char letraAnterior;
    boolean ncMode = false;

    static final  String categorias[] = {
            "ANIMAL",
            "CARRO",
            "TIME",
            "PAÍS",
            "FRUTA"
    };
    static final String[][] palavras = {{
        //ANIMAIS
            "GATO",
            "CACHORRO",
            "COELHO",
            "ELEFANTE",
            "ESQUILO",
            "ZEBRA",
            "POMBO",
            "HIPOPOTAMO",
            "COBRA",
            "MACACO",
            "CABRA",
            "JUMENTO",
            "CROCODILO",
        },{
        },{ //TIMES
            "CORINTHIANS",
            "SANTOS",
            "PALMEIRAS",
            "FLAMENGO",
            "FLUMINENSE",
            "GREMIO",
            "VASCO",

        },{ //PAIS
            "BRASIL",
            "ITALIA",
            "HOLANDA",
            "TURQUIA",
            "INGLATERRA",
            "CHINA",
            "AUSTRALIA",

        },{ //FRUTAS
            "BANANA",
            "LARANJA",
            "MARACUJA",
            "UVA",
            "KIWI",
            "CARAMBOLA",
            "CEREJA",
            "MORANGO",
            "PESSEGO",
            "MANGA",
            "JABUTICABA",
            "AMEIXA"
        }
    };

    static final int VITORIA = 0;
    static final int DERROTA = 1;

    int Categoria = escolherCategoria();
    String categoriaEscolhida = categorias[Categoria];
    String palavraSorteada = escolherPalavra(Categoria);

    int totalAcertos = palavraSorteada.length();
    int totalErros = 6;

    int qntErro = 0;
    int qntAcertos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo);

        iniciarMusica();

        txtPalavraJogo = findViewById(R.id.txtPalavraJogo);
        txtErros = findViewById(R.id.txtErros);
        txtAcertos = findViewById(R.id.txtAcertos);
        txtCategoria = findViewById(R.id.txtCategoria);
        imgForca = findViewById(R.id.imgForca);

        txtCategoria.setText(categoriaEscolhida);

        for(int i = 0; i <= palavraSorteada.length() - 1; i++){
            if(i == palavraSorteada.length() - 1){
                palavra += "_";
            }else{
                palavra += "_ ";
            }
        }
        txtPalavraJogo.setText(palavra);

        atualizarQuantidaDeAcertos();
        atualizarQuantidaDeErros();
    }

    public void acaoBotao(View v) {
        letraEscolhida = v.getTag().toString().charAt(0);
        v.setEnabled(false);

        if(letraEscolhida == 'C' && letraAnterior == 'N'){
            ncMode = true;
        }

        letraAnterior = letraEscolhida;

        boolean erro = true;

        for(int i = 0; i <= palavraSorteada.length() - 1; i++){
            if(letraEscolhida == palavraSorteada.charAt(i)){
                StringBuilder novaPalavra = new StringBuilder(palavra);
                novaPalavra.setCharAt(i * 2, letraEscolhida);
                palavra = novaPalavra.toString();
                txtPalavraJogo.setText(palavra);
                qntAcertos += 1;
                erro = false;
            }
        }
        if(erro){
            v.setBackgroundColor(getResources().getColor(R.color.desativadovermelho));
            qntErro += 1;
            atualizarQuantidaDeErros();
            if(qntErro == 6){
                txtErros.setTextColor(getResources().getColor(R.color.desativadovermelho));
            }
        }else{
            v.setBackgroundColor(getResources().getColor(R.color.desativadoverde));
            atualizarQuantidaDeAcertos();
            if(qntAcertos == totalAcertos - 1){
                txtAcertos.setTextColor(getResources().getColor(R.color.desativadoverde));
            }
        }
        atualizarImagem();

        if(qntErro > totalErros){
            gameOver(DERROTA);
        }else if(qntAcertos >= totalAcertos){
            gameOver(VITORIA);
        }
    }

    public void reiniciar(View v){
        pararMusica();
        recreate();
    }

    private void gameOver(int condicaoVitoria){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        pararMusica();

        if(condicaoVitoria == VITORIA){
            alert.setTitle("Você Ganhou \uD83D\uDE00");
            alert.setMessage("Você acertou a palavra. Parabéns.");
        }else{
            alert.setTitle("Você Perdeu \uD83D\uDE22");
            alert.setMessage("Você errou a palavra. A palavra é '" + palavraSorteada + "'.");
        }
        alert.setNegativeButton("sair", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.setPositiveButton("reiniciar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recreate();
            }
        });

        alert.create().show();
    }

    private void atualizarQuantidaDeAcertos(){
        txtAcertos.setText("Acertos: " + qntAcertos);
    }

    private void atualizarQuantidaDeErros(){
        txtErros.setText("Erros: " + qntErro + "/" + totalErros);
    }


    private int escolherCategoria(){
        int min = 0;
        int max = categorias.length - 1;

        return new Random().nextInt((max - min) + 1) + min;
    }
    private String escolherPalavra(int numeroCategoria){
        int min = 0;
        int max = palavras[numeroCategoria].length - 1;
        final int numeroPalavra = new Random().nextInt((max - min) + 1) + min;

        return palavras[numeroCategoria][numeroPalavra];
    }

    @Override
    public void onBackPressed() {
        pararMusica();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void atualizarImagem(){
        if(ncMode){
            if(qntErro == 1){
                imgForca.setImageResource(R.drawable.nc1);
            }else if(qntErro == 2){
                imgForca.setImageResource(R.drawable.nc2);
            }else if(qntErro == 3){
                imgForca.setImageResource(R.drawable.nc3);
            }else if(qntErro == 4){
                imgForca.setImageResource(R.drawable.nc4);
            }else if(qntErro == 5){
                imgForca.setImageResource(R.drawable.nc5);
            }else if(qntErro == 6){
                imgForca.setImageResource(R.drawable.nctotal);
            }
        }else{
            if(qntErro == 1){
                imgForca.setImageResource(R.drawable.hm1);
            }else if(qntErro == 2){
                imgForca.setImageResource(R.drawable.hm2);
            }else if(qntErro == 3){
                imgForca.setImageResource(R.drawable.hm3);
            }else if(qntErro == 4){
                imgForca.setImageResource(R.drawable.hm4);
            }else if(qntErro == 5){
                imgForca.setImageResource(R.drawable.hm5);
            }else if(qntErro == 6){
                imgForca.setImageResource(R.drawable.hmtotal);
            }
        }
    }

    public void iniciarMusica(){
        musica = MediaPlayer.create(getApplicationContext(),R.raw.jogo);
        musica.start();
    }

    public void pararMusica(){
        musica.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pararMusica();
    }

    @Override
    protected void onResume() {
        super.onResume();
        musica.setLooping(true);
        musica.start();
    }
}