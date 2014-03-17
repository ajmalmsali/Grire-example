
package org.grire.Resources;

import org.grire.Resources.CEDDtools.Fuzzy10Bin;
import org.grire.Resources.CEDDtools.CompactCEDDQuant;
import org.grire.Resources.CEDDtools.Neighborhood;
import org.grire.Resources.CEDDtools.CEDDQuant;
import org.grire.Resources.CEDDtools.Fuzzy24Bin;
import org.grire.Resources.CEDDtools.RGB2HSV;
import org.grire.Resources.CEDDtools.MaskResults;
import java.awt.image.BufferedImage;

/**
 * The CEDD feature was created, implemented and provided by Savvas A. Chatzichristofis<br/>
 * More information can be found in: Savvas A. Chatzichristofis and Yiannis S. Boutalis,
 * <i>CEDD: Color and Edge Directivity VisualWordDescriptor. A Compact
 * VisualWordDescriptor for Image Indexing and Retrieval</i>, A. Gasteratos, M. Vincze, and J.K.
 * Tsotsos (Eds.): ICVS 2008, LNCS 5008, pp. 312-322, 2008.
 *
 * @author: Savvas A. Chatzichristofis, savvash@gmail.com
 */
public class CEDD {
    public float T0;
    public float T1;
    public float T2;
    public float T3;
    public boolean Compact = false;
    public float[] data = null;

    public CEDD(float Th0, float Th1, float Th2, float Th3, boolean CompactDescriptor) {
        this.T0 = Th0;
        this.T1 = Th1;
        this.T2 = Th2;
        this.T3 = Th3;
        this.Compact = CompactDescriptor;
    }

    public CEDD() {
        this.T0 = 14;
        this.T1 = 0.68f;
        this.T2 = 0.98f;
        this.T3 = 0.98f;
    }

    // Apply filter
    // signature changed by mlux
    public void extract(BufferedImage image) {
        Fuzzy10Bin Fuzzy10 = new Fuzzy10Bin(false);
        Fuzzy24Bin Fuzzy24 = new Fuzzy24Bin(false);
        RGB2HSV HSVConverter = new RGB2HSV();
        int[] HSV;

        float[] Fuzzy10BinResultTable;
        float[] Fuzzy24BinResultTable;
        float[] CEDD = new float[144];

        int width = image.getWidth();
        int height = image.getHeight();


        float[][] ImageGrid = new float[width][height];
        float[][] PixelCount = new float[2][2];
        int[][] ImageGridRed = new int[width][height];
        int[][] ImageGridGreen = new int[width][height];
        int[][] ImageGridBlue = new int[width][height];
        int NumberOfBlocks = 1600;
        int Step_X = (int) Math.floor(width / Math.sqrt(NumberOfBlocks));
        int Step_Y = (int) Math.floor(height / Math.sqrt(NumberOfBlocks));

        if ((Step_X % 2) != 0) {
            Step_X = Step_X - 1;
        }
        if ((Step_Y % 2) != 0) {
            Step_Y = Step_Y - 1;
        }

        if (Step_Y < 2) Step_Y = 2;
        if (Step_X < 2) Step_X = 2;

        int[] Edges = new int[6];

        MaskResults MaskValues = new MaskResults();
        Neighborhood PixelsNeighborhood = new Neighborhood();

        for (int i = 0; i < 144; i++) {
            CEDD[i] = 0;
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);
                ImageGridRed[x][y] = (pixel >> 16) & 0xff;
                ImageGridGreen[x][y] = (pixel >> 8) & 0xff;
                ImageGridBlue[x][y] = (pixel) & 0xff;
                int mean = (int) (0.114 * ImageGridBlue[x][y] + 0.587 * ImageGridGreen[x][y] + 0.299 * ImageGridRed[x][y]);
                ImageGrid[x][y] = mean;
            }
        }


        int[] CororRed = new int[Step_Y * Step_X];
        int[] CororGreen = new int[Step_Y * Step_X];
        int[] CororBlue = new int[Step_Y * Step_X];

        int[] CororRedTemp = new int[Step_Y * Step_X];
        int[] CororGreenTemp = new int[Step_Y * Step_X];
        int[] CororBlueTemp = new int[Step_Y * Step_X];

        int MeanRed, MeanGreen, MeanBlue;

        for (int y = 0; y < height - Step_Y; y += Step_Y) {
            for (int x = 0; x < width - Step_X; x += Step_X) {


                MeanRed = 0;
                MeanGreen = 0;
                MeanBlue = 0;
                PixelsNeighborhood.Area1 = 0;
                PixelsNeighborhood.Area2 = 0;
                PixelsNeighborhood.Area3 = 0;
                PixelsNeighborhood.Area4 = 0;
                Edges[0] = -1;
                Edges[1] = -1;
                Edges[2] = -1;
                Edges[3] = -1;
                Edges[4] = -1;
                Edges[5] = -1;

                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        PixelCount[i][j] = 0;
                    }
                }

                int TempSum = 0;

                for (int i = y; i < y + Step_Y; i++) {
                    for (int j = x; j < x + Step_X; j++) {

                        CororRed[TempSum] = ImageGridRed[j][i];
                        CororGreen[TempSum] = ImageGridGreen[j][i];
                        CororBlue[TempSum] = ImageGridBlue[j][i];

                        CororRedTemp[TempSum] = ImageGridRed[j][i];
                        CororGreenTemp[TempSum] = ImageGridGreen[j][i];
                        CororBlueTemp[TempSum] = ImageGridBlue[j][i];

                        TempSum++;

                        if (j < (x + Step_X / 2) && i < (y + Step_Y / 2))
                            PixelsNeighborhood.Area1 += 4 * ImageGrid[j][i] / (Step_X * Step_Y);
                        if (j >= (x + Step_X / 2) && i < (y + Step_Y / 2))
                            PixelsNeighborhood.Area2 += 4 * ImageGrid[j][i] / (Step_X * Step_Y);
                        if (j < (x + Step_X / 2) && i >= (y + Step_Y / 2))
                            PixelsNeighborhood.Area3 += 4 * ImageGrid[j][i] / (Step_X * Step_Y);
                        if (j >= (x + Step_X / 2) && i >= (y + Step_Y / 2))
                            PixelsNeighborhood.Area4 += 4 * ImageGrid[j][i] / (Step_X * Step_Y);
                    }
                }

                MaskValues.Mask1 = Math.abs(PixelsNeighborhood.Area1 * 2 + PixelsNeighborhood.Area2 * -2 + PixelsNeighborhood.Area3 * -2 + PixelsNeighborhood.Area4 * 2);
                MaskValues.Mask2 = Math.abs(PixelsNeighborhood.Area1 * 1 + PixelsNeighborhood.Area2 * 1 + PixelsNeighborhood.Area3 * -1 + PixelsNeighborhood.Area4 * -1);
                MaskValues.Mask3 = Math.abs(PixelsNeighborhood.Area1 * 1 + PixelsNeighborhood.Area2 * -1 + PixelsNeighborhood.Area3 * 1 + PixelsNeighborhood.Area4 * -1);
                MaskValues.Mask4 = (float) Math.abs(PixelsNeighborhood.Area1 * Math.sqrt(2) + PixelsNeighborhood.Area2 * 0 + PixelsNeighborhood.Area3 * 0 + PixelsNeighborhood.Area4 * -Math.sqrt(2));
                MaskValues.Mask5 = (float) Math.abs(PixelsNeighborhood.Area1 * 0 + PixelsNeighborhood.Area2 * Math.sqrt(2) + PixelsNeighborhood.Area3 * -Math.sqrt(2) + PixelsNeighborhood.Area4 * 0);

                float Max = Math.max(MaskValues.Mask1, Math.max(MaskValues.Mask2, Math.max(MaskValues.Mask3, Math.max(MaskValues.Mask4, MaskValues.Mask5))));

                MaskValues.Mask1 = MaskValues.Mask1 / Max;
                MaskValues.Mask2 = MaskValues.Mask2 / Max;
                MaskValues.Mask3 = MaskValues.Mask3 / Max;
                MaskValues.Mask4 = MaskValues.Mask4 / Max;
                MaskValues.Mask5 = MaskValues.Mask5 / Max;

                int T;

                if (Max < T0) {
                    Edges[0] = 0;
                    T = 0;
                } else {
                    T = -1;

                    if (MaskValues.Mask1 > T1) {
                        T++;
                        Edges[T] = 1;
                    }
                    if (MaskValues.Mask2 > T2) {
                        T++;
                        Edges[T] = 2;
                    }
                    if (MaskValues.Mask3 > T2) {
                        T++;
                        Edges[T] = 3;
                    }
                    if (MaskValues.Mask4 > T3) {
                        T++;
                        Edges[T] = 4;
                    }
                    if (MaskValues.Mask5 > T3) {
                        T++;
                        Edges[T] = 5;
                    }

                }

                for (int i = 0; i < (Step_Y * Step_X); i++) {
                    MeanRed += CororRed[i];
                    MeanGreen += CororGreen[i];
                    MeanBlue += CororBlue[i];
                }

                MeanRed = (int) (MeanRed / (Step_Y * Step_X));
                MeanGreen = (int) (MeanGreen / (Step_Y * Step_X));
                MeanBlue = (int) (MeanBlue / (Step_Y * Step_X));

                HSV = HSVConverter.ApplyFilter(MeanRed, MeanGreen, MeanBlue);

                if (this.Compact == false) {
                    Fuzzy10BinResultTable = Fuzzy10.ApplyFilter(HSV[0], HSV[1], HSV[2], 2);
                    Fuzzy24BinResultTable = Fuzzy24.ApplyFilter(HSV[0], HSV[1], HSV[2], Fuzzy10BinResultTable, 2);

                    for (int i = 0; i <= T; i++) {
                        for (int j = 0; j < 24; j++) {
                            if (Fuzzy24BinResultTable[j] > 0) CEDD[24 * Edges[i] + j] += Fuzzy24BinResultTable[j];
                        }
                    }
                } else {
                    Fuzzy10BinResultTable = Fuzzy10.ApplyFilter(HSV[0], HSV[1], HSV[2], 2);
                    for (int i = 0; i <= T; i++) {
                        for (int j = 0; j < 10; j++) {
                            if (Fuzzy10BinResultTable[j] > 0) CEDD[10 * Edges[i] + j] += Fuzzy10BinResultTable[j];
                        }
                    }
                }
            }
        }

        float Sum = 0;
        for (int i = 0; i < 144; i++) {
            Sum += CEDD[i];
        }

        for (int i = 0; i < 144; i++) {
            CEDD[i] = CEDD[i] / Sum;
        }

        float qCEDD[];


        if (Compact == false) {
            CEDDQuant quants = new CEDDQuant();
            qCEDD = quants.Apply(CEDD);
        } else {
            CompactCEDDQuant quants = new CompactCEDDQuant();
            qCEDD = quants.Apply(CEDD);
        }

        data = qCEDD;

    }
}