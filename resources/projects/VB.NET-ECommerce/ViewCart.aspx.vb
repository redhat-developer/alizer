Imports System.Data
Imports System.Data.SqlClient

Partial Class ViewCart
    Inherits System.Web.UI.Page
    Protected Sub Page_Load(sender As Object, e As EventArgs) Handles Me.Load
        Dim strCartID As String
        If HttpContext.Current.Request.Cookies("CartID") Is Nothing Then
            strCartID = GetRandomID(10)
            Dim CookieTo As New HttpCookie("CartID", strCartID)
            HttpContext.Current.Response.AppendCookie(CookieTo)
        Else
            Dim CookieBack As HttpCookie
            CookieBack = HttpContext.Current.Request.Cookies("CartID")
            strCartID = CookieBack.Value
        End If
        ViewCart.SelectCommand = "SELECT * FROM Cart WHERE CartID = '" + strCartID + "'"
        ViewCart.DataBind()
        Dim dr As SqlDataReader
        Dim strSQLStatement As String
        Dim strSQL As SqlCommand
        Dim strConnectionString As String = "Data Source=(LocalDB)\v11.0;AttachDbFilename=|DataDirectory|\Category.mdf;Integrated Security=True"
        strSQLStatement = "SELECT SUM(UnitPrice * Quantity) AS Subtotal FROM Cart WHERE CartID = '" + strCartID + "'"
        Dim conn As New SqlConnection(strConnectionString)
        strSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = strSQL.ExecuteReader()
        If dr.Read() Then
            subtotal.Text = dr.Item("Subtotal")
            subtotal.DataBind()
        End If
        conn.Close()
    End Sub
    Public Function GetRandomID(ByVal length As Integer) As String
        Dim guidResult As String = System.Guid.NewGuid().ToString()
        guidResult = guidResult.Replace("-", String.Empty)
        If length <= 0 OrElse length > guidResult.Length Then
            Throw New ArgumentException("Length must be between 1 and " & guidResult.Length)
        End If
        Return guidResult.Substring(0, length)
    End Function
    Sub gvCartLine_RowCommand(ByVal sender As Object, ByVal e As GridViewCommandEventArgs)
        Dim gvrCartLine As GridViewRow = DirectCast(DirectCast(e.CommandSource, Button).NamingContainer, GridViewRow)
        Dim intRowIndex As Integer = gvrCartLine.RowIndex
        Dim selectedRow As GridViewRow = GridView1.Rows(intRowIndex)
        Dim tablecellQuantity As TableCell = selectedRow.Cells(2)
        Dim tbNewQuantity As TextBox = CType(GridView1.Rows(intRowIndex).Cells(2).FindControl("tbNewQuantity"), TextBox)
        If (e.CommandName = "rowUpdate" And IsNumeric(tbNewQuantity.Text)) Or (e.CommandName = "rowRemove") Then
            If e.CommandName = "rowRemove" And Not IsNumeric(tbNewQuantity) Then
                Dim intNewQuantity As String = CStr(tbNewQuantity.Text)
            Else
                Dim intNewQuantity As Integer = CInt(tbNewQuantity.Text)
            End If
            Dim tablecellProductCode As TableCell = selectedRow.Cells(0)
            Dim strProductCode As String = tablecellProductCode.Text
            Dim strCartID As String
            Dim CookieBack As HttpCookie
            CookieBack = HttpContext.Current.Request.Cookies("CartID")
            strCartID = CookieBack.Value
            Dim dr As SqlDataReader
            Dim strSQLStatement As String
            Dim strSQL As SqlCommand
            Dim strConnectionString As String = "Data Source=(LocalDB)\v11.0;AttachDbFilename=|DataDirectory|\Category.mdf;Integrated Security=True"
            If e.CommandName = "rowUpdate" Then
                strSQLStatement = "UPDATE Cart SET Quantity = " & tbNewQuantity.Text & " WHERE CartID = '" & strCartID & "' AND ProductID = '" & strProductCode & "'; SELECT SUM(UnitPrice * Quantity) AS Subtotal FROM Cart WHERE CartID = '" + strCartID + "'"
                messages.Text = "Line(s) updated."
            ElseIf e.CommandName = "rowRemove" Then
                strSQLStatement = "DELETE FROM Cart WHERE CartID = '" & strCartID & "' AND ProductID = '" & strProductCode & "'"
                messages.Text = "Item removed."
            End If
            Dim conn As New SqlConnection(strConnectionString)
            strSQL = New SqlCommand(strSQLStatement, conn)
            conn.Open()
            dr = strSQL.ExecuteReader()
            If dr.Read() Then
                subtotal.Text = dr.Item("Subtotal")
                subtotal.DataBind()
            End If
            conn.Close()
            conn.Open()
            dr = strSQL.ExecuteReader(CommandBehavior.CloseConnection)
            GridView1.DataBind()
        Else
            messages.Text = "Quantity Error."
        End If
    End Sub

    Protected Sub btnEmpty_Click(sender As Object, e As EventArgs) Handles btnEmpty.Click
        Dim dr As SqlDataReader
        Dim strSQLStatement As String
        Dim cmdSQL As SqlCommand
        Dim strConnectionString As String = System.Configuration.ConfigurationManager.ConnectionStrings("CategoryConnection").ConnectionString
        strSQLStatement = "TRUNCATE TABLE Cart"
        Dim conn As New SqlConnection(strConnectionString)
        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()
        Response.Redirect("Category.aspx")

    End Sub
End Class

