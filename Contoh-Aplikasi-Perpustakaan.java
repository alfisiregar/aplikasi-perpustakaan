// Fungsi Pencarian Buku berdasarkan Judul atau Pengarang:

public List<Book> searchBook(String title) {
    // Koneksi ke basis data
    Connection connection = getConnection();
    List<Book> results = new ArrayList<>();
    
    try {
        // Query untuk mencari buku berdasarkan judul atau pengarang
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, "%" + title + "%");
        statement.setString(2, "%" + title + "%");
        
        // Eksekusi query
        ResultSet resultSet = statement.executeQuery();
        
        // Loop melalui hasil pencarian dan tambahkan ke daftar buku
        while (resultSet.next()) {
            Book book = new Book();
            book.setId(resultSet.getInt("id"));
            book.setTitle(resultSet.getString("title"));
            book.setAuthor(resultSet.getString("author"));
            // tambahkan informasi buku ke daftar hasil pencarian
            results.add(book);
        }
        
        // Tutup statement dan resultSet
        statement.close();
        resultSet.close();
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        // Tutup koneksi
        closeConnection(connection);
    }
    
    // Mengembalikan hasil pencarian
    return results;
}


// Fungsi Peminjaman Buku:
public boolean borrowBook(int memberId, int bookId) {
    // Koneksi ke basis data
    Connection connection = getConnection();
    
    try {
        // Periksa ketersediaan buku
        if (isBookAvailable(bookId)) {
            // Tambahkan data peminjaman ke tabel peminjaman
            String query = "INSERT INTO borrowings (member_id, book_id, borrow_date) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, memberId);
            statement.setInt(2, bookId);
            statement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            
            // Eksekusi query
            int rowsAffected = statement.executeUpdate();
            
            // Perbarui status buku menjadi dipinjam jika peminjaman berhasil
            if (rowsAffected > 0) {
                updateBookStatus(bookId, "Dipinjam");
                // Mengembalikan status peminjaman berhasil
                return true;
            }
            
            // Tutup statement
            statement.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        // Tutup koneksi
        closeConnection(connection);
    }
    
    // Mengembalikan status peminjaman gagal
    return false;
}


// Fungsi Pengembalian Buku:

public boolean returnBook(int memberId, int bookId) {
    // Koneksi ke basis data
    Connection connection = getConnection();
    
    try {
        // Periksa status peminjaman buku
        if (isBookBorrowed(bookId, memberId)) {
            // Hapus data peminjaman dari tabel peminjaman
            String query = "DELETE FROM borrowings WHERE member_id = ? AND book_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, memberId);
            statement.setInt(2, bookId);
            
            // Eksekusi query
            int rowsAffected = statement.executeUpdate();
            
            // Perbarui status buku menjadi tersedia jika pengembalian berhasil
            if (rowsAffected > 0) {
                updateBookStatus(bookId, "Tersedia");
                // Mengembalikan status pengembalian berhasil
                return true;
            }
            
            // Tutup statement
            statement.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        // Tutup koneksi
        closeConnection(connection);
    }
    
    // Mengembalikan status pengembalian gagal
    return false;
}


