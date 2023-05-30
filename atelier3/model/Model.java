package atelier3.model;

import java.util.List;
import java.util.ArrayList;

import atelier3.controller.OutputModelData;
import atelier3.nutsAndBolts.PieceSquareColor;

public class Model implements BoardGame<Coord> {

    private PieceSquareColor currentGamerColor;
    private ModelImplementor implementor;

    private boolean isRafleInProgress;
    private Coord previousRaflePieceCoord;

    private int scoreJoueurNoir;
    private int scoreJoueurBlanc;

    public Model() {
        super();
        this.implementor = new ModelImplementor();
        this.currentGamerColor = ModelConfig.BEGIN_COLOR;
        this.isRafleInProgress = false;
        this.previousRaflePieceCoord = null;
        System.out.println(this);
    }

    @Override
    public String toString() {
        return implementor.toString();
    }

    @Override
    public OutputModelData<Coord> moveCapturePromote(Coord toMovePieceCoord, Coord targetSquareCoord) {
        OutputModelData<Coord> outputModelData = null;
        boolean isMoveDone = false;
        Coord toCapturePieceCoord = null;
        Coord toPromotePieceCoord = null;
        PieceSquareColor toPromotePieceColor = null;

        // Vérifie si le joueur utilise seulement la pièce qui peut faire une rafle pendant les rafles
        if (isRafleInProgress && !toMovePieceCoord.equals(previousRaflePieceCoord)) {
            return outputModelData;
        }

        if (isPieceMoveable(toMovePieceCoord, targetSquareCoord)) {
            if (isThereMaxOnePieceOnItinerary(toMovePieceCoord, targetSquareCoord)) {
                toCapturePieceCoord = getToCapturePieceCoord(toMovePieceCoord, targetSquareCoord);
                boolean isPieceToCapture = toCapturePieceCoord != null;
                if (isMovePiecePossible(toMovePieceCoord, targetSquareCoord, isPieceToCapture)) {
                    movePiece(toMovePieceCoord, targetSquareCoord);
                    isMoveDone = true;
                    remove(toCapturePieceCoord);
                    if (isPiecePromotable(targetSquareCoord)) {
                        promotePiece(targetSquareCoord);
                        toPromotePieceCoord = targetSquareCoord;
                        toPromotePieceColor = this.currentGamerColor;
                    }
                    int blackScore = implementor.getBlackScore();
                    int whiteScore = implementor.getWhiteScore();

                    if (isMoveDone) {
                        List<Coord> rafleCoords = checkForRafle(targetSquareCoord);
                        if (rafleCoords.isEmpty()) {
                            switchGamer();
                        } else {
                            isRafleInProgress = true;
                            previousRaflePieceCoord = targetSquareCoord;
                        }
                    } else {
                        switchGamer();
                    }

                    outputModelData = new OutputModelData<Coord>(
                            isMoveDone,
                            toCapturePieceCoord,
                            toPromotePieceCoord,
                            toPromotePieceColor,
                            blackScore,
                            whiteScore);
                }
            }
        }
        System.out.println(this);

        return outputModelData;
    }

    private boolean isPieceMoveable(Coord toMovePieceCoord, Coord targetSquareCoord) {
        boolean bool = false;

        bool = implementor.isPiecehere(toMovePieceCoord)
                && implementor.getPieceColor(toMovePieceCoord) == currentGamerColor
                && Coord.coordonnees_valides(targetSquareCoord)
                && !implementor.isPiecehere(targetSquareCoord);

        return bool;
    }

    private boolean isThereMaxOnePieceOnItinerary(Coord toMovePieceCoord, Coord targetSquareCoord) {
        boolean isThereMaxOnePieceOnItinerary = false;

        List<Coord> coordsOnItinerary = implementor.getCoordsOnItinerary(toMovePieceCoord, targetSquareCoord);

        if (coordsOnItinerary != null) {

            int count = 0;
            Coord potentialToCapturePieceCoord = null;
            for (Coord coordOnItinerary : coordsOnItinerary) {
                if (implementor.isPiecehere(coordOnItinerary)) {
                    count++;
                    potentialToCapturePieceCoord = coordOnItinerary;
                }
            }

            if (count == 0 || (count == 1 && currentGamerColor != implementor.getPieceColor(potentialToCapturePieceCoord))) {
                isThereMaxOnePieceOnItinerary = true;
            }
        }

        return isThereMaxOnePieceOnItinerary;
    }

    private Coord getToCapturePieceCoord(Coord toMovePieceCoord, Coord targetSquareCoord) {
        Coord toCapturePieceCoord = null;
        List<Coord> coordsOnItinerary = implementor.getCoordsOnItinerary(toMovePieceCoord, targetSquareCoord);

        if (coordsOnItinerary != null) {

            int count = 0;
            Coord potentialToCapturePieceCoord = null;
            for (Coord coordOnItinerary : coordsOnItinerary) {
                if (implementor.isPiecehere(coordOnItinerary)) {
                    count++;
                    potentialToCapturePieceCoord = coordOnItinerary;
                }
            }

            if (count == 0 || (count == 1 && currentGamerColor != implementor.getPieceColor(potentialToCapturePieceCoord))) {
                toCapturePieceCoord = potentialToCapturePieceCoord;
            }
        }

        return toCapturePieceCoord;
    }

    private boolean isMovePiecePossible(Coord toMovePieceCoord, Coord targetSquareCoord, boolean isPieceToCapture) {
        return implementor.isMovePieceOk(toMovePieceCoord, targetSquareCoord, isPieceToCapture);
    }

    private void movePiece(Coord toMovePieceCoord, Coord targetSquareCoord) {
        implementor.movePiece(toMovePieceCoord, targetSquareCoord);
    }

    private void remove(Coord toCapturePieceCoord) {
        implementor.removePiece(toCapturePieceCoord);
    }

    private boolean isPiecePromotable(Coord targetSquareCoord) {
        return implementor.isPiecePromotable(targetSquareCoord);
    }

    private void promotePiece(Coord targetSquareCoord) {
        implementor.promotePiece(targetSquareCoord);
    }

    private void switchGamer() {
        currentGamerColor = currentGamerColor == PieceSquareColor.WHITE ? PieceSquareColor.BLACK : PieceSquareColor.WHITE;
        isRafleInProgress = false;
    }

    private List<Coord> getPossibleCaptures(Coord pieceCoord) {
        List<Coord> possibleCaptures = new ArrayList<>();

        int[] directions = new int[]{-1, 1};

        for (int i : directions) {
            for (int j : directions) {
                char nextColonne = (char) (pieceCoord.getColonne() + i);
                int nextLigne = pieceCoord.getLigne() + j;
                char afterNextColonne = (char) (pieceCoord.getColonne() + 2 * i);
                int afterNextLigne = pieceCoord.getLigne() + 2 * j;

                Coord next = new Coord(nextColonne, nextLigne);
                Coord afterNext = new Coord(afterNextColonne, afterNextLigne);

                if (Coord.coordonnees_valides(next) && Coord.coordonnees_valides(afterNext)
                        && implementor.isPiecehere(next)
                        && implementor.getPieceColor(next) != currentGamerColor
                        && !implementor.isPiecehere(afterNext)) {

                    possibleCaptures.add(afterNext);
                }
            }
        }

        return possibleCaptures;
    }

    private List<Coord> checkForRafle(Coord pieceCoord) {
        return getPossibleCaptures(pieceCoord);
    }
}