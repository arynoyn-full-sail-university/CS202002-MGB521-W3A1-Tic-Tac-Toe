package edu.fullsail.mgms.cse.tictactoe.christopherwest.classes;

public class PlayerStats {
    private byte player_token;
    private int[] row_count;
    private int[] column_count;
    private int diagonal_count;
    private int diagonal_inverse_count;
    private int board_size;

    public byte getPlayerToken() {
        return player_token;
    }

    public int[] getRowCount() {
        return row_count;
    }

    private void incrementRowCount(int row) {
        this.row_count[row]++;
    }

    public int[] getColumnCount() {
        return column_count;
    }

    private void incrementColumnCount(int column) {
        this.column_count[column]++;
    }

    public int getDiagonalCount() {
        return diagonal_count;
    }

    private void incrementDiagonalCount() {
        this.diagonal_count++;
    }

    public int getDiagonalInverseCount() {
        return diagonal_inverse_count;
    }

    private void incrementDiagonalInverseCount() {
        this.diagonal_inverse_count++;
    }

    public boolean hasPlayerWon(int boardIndex) {
        int row = getRow(boardIndex);
        int col = getColumn(boardIndex);
        return this.row_count[row] == this.board_size
                || this.column_count[col] == this.board_size
                || this.diagonal_count == this.board_size
                || this.diagonal_inverse_count == this.board_size;
    }

    public void move(byte[] board, int boardIndex) {
        board[boardIndex] = this.player_token;
        int row = getRow(boardIndex);
        int col = getColumn(boardIndex);

        this.incrementColumnCount(col);
        this.incrementRowCount(row);
        if(row == col) {
            this.incrementDiagonalCount();
        }
        if((row + 1) + col == this.board_size){
            this.incrementDiagonalInverseCount();
        }
    }

    private int getRow(int boardIndex) {
        return boardIndex / this.board_size;
    }

    private int getColumn(int boardIndex) {
        return boardIndex % this.board_size;
    }

    private int getBoardIndex(int row, int column) {
        return (row * this.board_size) + (column % this.board_size);
    }

    public void reset() {
        for (int i = 0; i < this.board_size; i++) {
            this.row_count[i] = 0;
            this.column_count[i] = 0;
        }
        this.diagonal_count = 0;
        this.diagonal_inverse_count = 0;
        this.player_token = (byte)'#';
    }

    public PlayerStats(int boardSize, Byte playerToken) {
        this.board_size = boardSize;
        this.row_count = new int[boardSize];
        this.column_count = new int[boardSize];
        this.reset();
        this.player_token = playerToken;
    }
}
