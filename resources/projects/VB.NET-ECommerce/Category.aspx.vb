Imports System.Data
Imports System.Data.SqlClient

Partial Class Default3

    Inherits System.Web.UI.Page


    Protected Sub Page_Load(sender As Object, e As EventArgs) Handles Me.Load
        If Request.QueryString("MainCategoryID") <> "" Then
            ' generate the subcategory
            SqlDSSubCategory.SelectCommand = "Select * From Category Where Parent = " & CInt(Request.QueryString("MainCategoryID"))
            Response.Write(SqlDSSubCategory.SelectCommand)
            rpSubCategory.DataBind()
            lblMainCategory.Text = Request.QueryString("CategoryName")
        End If
        If Request.QueryString("ProductID") <> "" Then
            SqlDSProduct.SelectCommand = "Select * From Product Where ProductID = " & CInt(Request.QueryString("ProductID"))
            SqlDSProduct.DataBind()
            ProductInfo.Visible = True
            Panel1.Visible = True

        End If

    End Sub

    Protected Sub Button1_Click(sender As Object, e As EventArgs) Handles Button1.Click
        If IsNumeric(TextBox1.Text) Then

            Dim dr As SqlDataReader
            Dim strSQLStatement As String
            Dim cmdSQL As SqlCommand
            Dim strConnectionString As String = System.Configuration.ConfigurationManager.ConnectionStrings("CategoryConnection").ConnectionString

            strSQLStatement = "SELECT * FROM Product WHERE ProductID = '" & CStr(Request.QueryString("ProductID") & "'")

            Dim conn As New SqlConnection(strConnectionString)
            cmdSQL = New SqlCommand(strSQLStatement, conn)
            conn.Open()
            dr = cmdSQL.ExecuteReader()



            Dim strProductName As String = ""
            Dim decPrice As Decimal


            If dr.Read() Then


                strProductName = dr.Item("ProductName")
                decPrice = dr.Item("UnitPrice")
            End If


            Dim strCartID As String


            If HttpContext.Current.Request.Cookies("CartID") Is Nothing Then

                strCartID = GetRandomPasswordUsingGUID(10)

                Dim CookieTo As New HttpCookie("CartID", strCartID)
                HttpContext.Current.Response.AppendCookie(CookieTo)
            Else

                Dim CookieBack As HttpCookie
                CookieBack = HttpContext.Current.Request.Cookies("CartID")
                strCartID = CookieBack.Value
            End If
            conn.Close()

            strSQLStatement = "SELECT * FROM Cart WHERE CartID = '" & strCartID & "' AND ProductID = '" & Request.QueryString("product_code") & "'"

            cmdSQL = New SqlCommand(strSQLStatement, conn)
            conn.Open()
            dr = cmdSQL.ExecuteReader()

            If dr.Read() Then
                conn.Close()
                strSQLStatement = "UPDATE Cart set Quantity = '" & TextBox1.Text & "' WHERE CartID = '" & strCartID & "' AND ProductID = '" & Request.QueryString("ProductID") & "'"
                cmdSQL = New SqlCommand(strSQLStatement, conn)
                conn.Open()
                dr = cmdSQL.ExecuteReader(CommandBehavior.CloseConnection)
            Else
                conn.Close()
                strSQLStatement = "INSERT INTO Cart (CartID, ProductID, ProductName, UnitPrice, Quantity) values('" & strCartID & "', '" & Request.QueryString("ProductID") & "', '" & strProductName & "', " & decPrice & ", " & TextBox1.Text & ")"
                cmdSQL = New SqlCommand(strSQLStatement, conn)
                conn.Open()
                dr = cmdSQL.ExecuteReader(CommandBehavior.CloseConnection)
            End If
            Response.Redirect("ViewCart.aspx")
        End If


    End Sub

    Public Function GetRandomPasswordUsingGUID(ByVal length As Integer) As String
        'Get the GUID
        Dim guidResult As String = System.Guid.NewGuid().ToString()

        'Remove the hyphens
        guidResult = guidResult.Replace("-", String.Empty)

        'Make sure length is valid
        If length <= 0 OrElse length > guidResult.Length Then
            Throw New ArgumentException("Length must be between 1 and " & guidResult.Length)
        End If

        'Return the first length bytes
        Return guidResult.Substring(0, length)
    End Function
End Class