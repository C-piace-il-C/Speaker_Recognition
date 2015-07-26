package it.unige.diten.dsp.speakerrecognition;
/*
 * FFT
 * Questa classe per il calcolo della FFT e' stata presa da internet
 * e lievemente modificata, tuttavia abbiamo deciso di non usarla per i seguenti motivi:
 * -    Implementare la DFT e' facile e permette di arrivare velocemente ad una release funzionante.
 * -    E' comunque auspicabile scrivere la FFT a mano per scopi didattici, ma essendo time
 *      consuming conviene prendersi del tempo per farlo in futuro in modo da sostituirla alla DFT.
 */
public class FFT {

    // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) return new Complex[]{x[0]};

        // radix 2 Cooley-Tukey FFT
        if ((N & -N) != N) {
            throw new RuntimeException("N is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++) {
            even[k] = x[2 * k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd = even;  // reuse the array
        for (int k = 0; k < N / 2; k++) {
            odd[k] = x[2 * k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + N / 2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
}