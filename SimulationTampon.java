// =========================
// 1) Tampon circulaire (capacité 5)
// =========================
class TamponCirculaire {
    private final int[] buffer = new int[5];
    private int debut = 0, fin = 0, nbElements = 0;

    public synchronized void produire(int valeur) throws InterruptedException {
        while (nbElements == buffer.length) {
            wait();
        }

        buffer[fin] = valeur;
        fin = (fin + 1) % buffer.length;
        nbElements++;
        System.out.println("Produit : " + valeur + " | nbElements=" + nbElements);
        notifyAll();
    }

    public synchronized int consommer() throws InterruptedException {
        while (nbElements == 0) {
            wait();
        }

        int valeur = buffer[debut];
        debut = (debut + 1) % buffer.length;
        nbElements--;
        System.out.println("Consommé : " + valeur + " | nbElements=" + nbElements);
        notifyAll();
        return valeur;
    }
}

// =========================
// 2) 2 Producteurs : chacun produit 10 valeurs
// =========================
class Producteur extends Thread {
    private final TamponCirculaire tampon;
    private final int nb;

    public Producteur(String nom, TamponCirculaire tampon, int nb) {
        super(nom);
        this.tampon = tampon;
        this.nb = nb;
    }

    @Override
    public void run() {
        for (int i = 1; i <= nb; i++) {
            int valeur = (getName().hashCode() & 0x7fffffff) % 1000 + i;
            try {
                tampon.produire(valeur);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

// =========================
// 3) 2 Consommateurs : chacun consomme 10 valeurs
// =========================
class Consommateur extends Thread {
    private final TamponCirculaire tampon;
    private final int nb;

    public Consommateur(String nom, TamponCirculaire tampon, int nb) {
        super(nom);
        this.tampon = tampon;
        this.nb = nb;
    }

    @Override
    public void run() {
        for (int i = 1; i <= nb; i++) {
            try {
                int v = tampon.consommer();
                Thread.sleep(150);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

// =========================
// 4) Main : 2 producteurs + 2 consommateurs
// =========================
public class SimulationTampon {
    public static void main(String[] args) throws InterruptedException {
        TamponCirculaire tampon = new TamponCirculaire();

        Producteur p1 = new Producteur("P1", tampon, 10);
        Producteur p2 = new Producteur("P2", tampon, 10);

        Consommateur c1 = new Consommateur("C1", tampon, 10);
        Consommateur c2 = new Consommateur("C2", tampon, 10);

        p1.start(); p2.start();
        c1.start(); c2.start();

        p1.join(); p2.join();
        c1.join(); c2.join();

        System.out.println("Fin simulation.");
    }
}
