
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    private Picture picture;
    private double[][] energies;
    private final double BORDER_ENERGY = 1000.00;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture){
        if(picture == null) throw new IllegalArgumentException();
        this.picture = new Picture(picture);
        this.energies = new double[this.picture.height()][this.picture.width()];

        // calculate energy
        this.energies = calculateEnergy();
    }

    // current picture
    public Picture picture(){
        return new Picture(this.picture);
    }

    // width of current picture
    public int width(){
        return this.picture.width();
    }

    // height of current picture
    public int height(){
        return this.picture.height();
    }

    // energy of pixel at row x and column y
    public double energy(int col, int row){
        if(col < 0 || row < 0 || col >= width() || row >= height()){
            throw new IllegalArgumentException();
        }
        // is border
        if(col == 0 || col == this.picture.width()-1 || row == 0 || row == this.picture.height()-1){
            return BORDER_ENERGY;
        }
        double xDiff = calculateRGBDif(picture.get(col - 1, row),picture.get(col + 1, row));
        double yDiff = calculateRGBDif(picture.get(col, row - 1),picture.get(col, row + 1));

        double energy = Math.sqrt(xDiff + yDiff);

        return energy;
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam(){
        //init
        int[][] fromPixel = new int[height()][width()];
        double[][] distTo = new double[height()][width()];

        for (int row = 0; row < height(); row++){
            for(int col=0; col< width(); col++){
                if(row==0){
                    distTo[row][col] = BORDER_ENERGY;
                }else{
                    distTo[row][col] = Double.POSITIVE_INFINITY;
                }
            }
        }

        // relax
        for(int row=0; row < height()-1; row++){
            for(int col=1; col< width()-1; col++){
                relax(distTo,fromPixel, row, col, row+1,col-1); // left bottom
                relax(distTo,fromPixel, row, col, row+1,col); // bottom
                relax(distTo,fromPixel, row, col, row+1,col+1); // right bottom
            }
        }

        // get verticle seam
        // find smallest energy in the last row
        double min = Double.POSITIVE_INFINITY;
        int minCol = 0;

        for(int col=0;col<width()-1;col++){
            if(distTo[height()-1][col] < min){
                min = distTo[height()-1][col];
                minCol = col;
            }
        }

        // find all seam
        int[] seam = new int[height()];

        seam[height()-1] = minCol; // last row seam

        for(int row =height()-1; row>=1;row--){
            minCol = fromPixel[row][minCol]; // find the fromPixel (which parent connects to this minCol) of the minCol
            seam[row-1] = minCol; // 0 ~ last-1 row seam
        }

        return seam;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam(){
        transpose();
        int[] seam = findVerticalSeam();
        transpose();

        return seam;

    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){
        if(seam == null) throw new IllegalArgumentException();
        checkSeam(seam, false);

        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam){
        if(seam == null) throw new IllegalArgumentException();
        checkSeam(seam, true);

        // create new picture and set color
        Picture seamPicture = new Picture(width()-1,height());

        for(int row= 0; row < seamPicture.height(); row++){
            for(int col=0; col < seamPicture.width(); col++){
                if(col < seam[row]){
                    seamPicture.set(col,row,this.picture.get(col,row));
                }else{
                    seamPicture.set(col,row,this.picture.get(col+1,row));
                }
            }
        }
        this.picture = seamPicture;

        // recalculate energy for new picture
        this.energies = new double[this.energies.length][this.energies[0].length-1]; // height not changed, width remove 1
        this.energies = calculateEnergy();
    }

    //calculate energy
    private double[][] calculateEnergy(){
        for (int row = 0; row < energies.length; row++) {
            for (int col = 0; col < energies[0].length; col++){
                energies[row][col] = energy(col,row);
            }
        }
        return energies;
    }

    private double calculateRGBDif(Color a, Color b){
        double redDif = a.getRed() - b.getRed();
        double greenDif = a.getGreen() - b.getGreen();
        double blueDif = a.getBlue() - b.getBlue();

        double total = Math.pow(redDif,2) + Math.pow(greenDif,2) + Math.pow(blueDif,2);

        return total;
    }

    private void relax(double[][] distTo, int[][] edgeTo, int row, int col, int adjRow, int adjCol)
    {
        boolean lastrow = adjRow == height()-1;
        boolean isInBorder = adjRow < height() && adjCol < width() && adjRow >= 0 && adjCol >= 0;

        if(!isInBorder || (!lastrow && energies[adjRow][adjCol] == BORDER_ENERGY)){
            return;
        }

        if (distTo[adjRow][adjCol] > distTo[row][col] + energies[adjRow][adjCol]) // update distTo[adjRow][adjCol] if new parent energy is shorter
        {
            distTo[adjRow][adjCol] = distTo[row][col] + energies[adjRow][adjCol];
            edgeTo[adjRow][adjCol] = col; // remember the parent vertex col
        }
    }

    // swap row col to col row
    private void transpose(){
        Picture transposePic = new Picture(height(),width());
        double[][] transpose = new double[width()][height()];

        for(int i=0;i<height();i++){
            for(int j=0;j<width();j++){
                transposePic.set(i,j,this.picture.get(j,i));
                transpose[j][i] = energies[i][j];
            }
        }
        this.energies = transpose;
        this.picture = transposePic;
    }

    private void checkSeam(int[] seam, boolean isVerticle){
        for(int i=0;i < seam.length;i++){
            if(i < seam.length-1 && Math.abs(seam[i]-seam[i+1]) !=1 && Math.abs(seam[i]-seam[i+1]) !=0 ){ // neighbor seam only has 0 or 1 difference
                throw new IllegalArgumentException("invalid neighbor seam");
            }
            if(seam[i] < 0 || seam[i] >= width() && isVerticle || seam[i] >= height() && !isVerticle){ // array out of bound
                throw new IllegalArgumentException("seam value is out of bound");
            }
            if(isVerticle && seam.length != height() || !isVerticle && seam.length != width()){ // array out of bound
                throw new IllegalArgumentException("seam length should be equal to picture width or height");
            }
        }
    }

}
