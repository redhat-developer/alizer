Imports System.Data
Imports System.Data.SqlClient
Imports System.Net.Mail

Partial Class Receipt
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(sender As Object, e As EventArgs) Handles Me.Load

        Dim strCartID As String
        Dim CookieBack As HttpCookie
        CookieBack = HttpContext.Current.Request.Cookies("CartID")
        strCartID = CookieBack.Value


        Dim dr As SqlDataReader
        Dim strSQLStatement As String
        Dim cmdSQL As SqlCommand

        Dim strConnectionString As String = "Data Source=(LocalDB)\v11.0;AttachDbFilename=|DataDirectory|\Category.mdf;Integrated Security=True"

        strSQLStatement = "SELECT * FROM Customer WHERE OrderlineID = '" + strCartID + "'"


        Dim conn As New SqlConnection(strConnectionString)
        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()

        Dim email As String = ""

        If dr.Read() Then

            FirstName.Text = dr.Item("FirstName")
            LastName.Text = dr.Item("LastName")
            email = dr.Item("Email")
        End If
        conn.Close()


        ViewOrderline.SelectCommand = "SELECT * FROM Orderline WHERE OrderlineID = '" + strCartID + "'"

        ViewOrderline.DataBind()

        strSQLStatement = "SELECT * FROM OrderInfo WHERE OrderlineID = '" + strCartID + "'"


        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()

        If dr.Read() Then

            Subtotal.Text = dr.Item("Subtotal")
            Total.Text = dr.Item("Total")
        End If
        conn.Close()




        strSQLStatement = "SELECT * FROM Orderline WHERE OrderlineID = '" + strCartID + "'"


        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()

        If dr.Read() Then

        End If
        conn.Close()
        strSQLStatement = "DELETE FROM Cart WHERE CartID = '" + strCartID + "'"


        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()
        conn.Close()
        CookieBack.Expires = DateTime.Now.AddDays(-1D)
        Response.Cookies.Add(CookieBack)
    End Sub
End Class
