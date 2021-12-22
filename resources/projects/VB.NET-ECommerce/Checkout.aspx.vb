Imports System.Data
Imports System.Data.SqlClient
Imports System.Net
Imports System.IO
Imports System.Net.Mail
Imports System.Collections.Generic

Partial Class Checkout
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(sender As Object, e As EventArgs) Handles Me.Load
        Dim strCartID As String

        Dim CookieBack As HttpCookie
        CookieBack = HttpContext.Current.Request.Cookies("CartID")

        strCartID = CookieBack.Value

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

    Protected Sub SubmitCheckout_Click(ByVal sender As Object, ByVal e As EventArgs) Handles SubmitCheckout.Click

        Dim strCartID As String
        Dim CookieBack As HttpCookie
        CookieBack = HttpContext.Current.Request.Cookies("CartID")
        strCartID = CookieBack.Value

        Dim dr As SqlDataReader
        Dim strSQLStatement As String
        Dim cmdSQL As SqlCommand

        Dim strConnectionString As String = "Data Source=(LocalDB)\v11.0;AttachDbFilename=|DataDirectory|\Category.mdf;Integrated Security=True"

        Dim CreditCardDate As String = CStr(CreditCardExpirationMonth.Text + "/" + CreditCardExpirationYear.Text)

        strSQLStatement = "INSERT INTO Customer (OrderLineID, FirstName, LastName, Email, StreetAddress, City, State, Zip, PhoneNumber, CreditCardNumber, CreditCardType, CreditCardExpirationDate) VALUES ('" + strCartID + "', '" + FirstName.Text + "', '" + LastName.Text + "', '" + Email.Text + "', '" + StreetAddress.Text + "', '" + City.Text + "', '" + State.Text + "', '" + Zip.Text + "', '" + PhoneNumber.Text + "', '" + CreditCardNumber.Text + "', '" + CreditCardType.Text + "', '" + CStr(CreditCardDate) + "'); SELECT @@Identity;"

        Dim conn As New SqlConnection(strConnectionString)
        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()
        conn.Close()



        strSQLStatement = "SELECT * FROM Cart WHERE CartID = '" + strCartID + "'"

        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()


        Dim strProductCode As String = ""
        Dim strProductName As String = ""
        Dim decProductPrice As Decimal
        Dim intQuantity As Integer

        Dim products As String = ""

        Dim myList As New List(Of String)

        While dr.Read()
            strProductCode = dr.Item("ProductID")
            strProductName = dr.Item("ProductName")
            decProductPrice = dr.Item("UnitPrice")
            intQuantity = dr.Item("Quantity")

            products += dr.Item("ProductName") + " <br />"

            myList.Add("INSERT INTO Orderline (OrderlineID, ProductCode, ProductName, UnitPrice, Quantity) VALUES ('" & strCartID & "', '" & CStr(strProductCode) & "', '" & CStr(strProductName) & "', " & CDec(decProductPrice) & ", " & CInt(intQuantity) & ")")

        End While
        conn.Close()



        For Each myListing In myList
            cmdSQL = New SqlCommand(myListing, conn)
            conn.Open()
            dr = cmdSQL.ExecuteReader()
            conn.Close()
        Next


        strSQLStatement = "SELECT SUM(UnitPrice * Quantity) AS Subtotal FROM Cart WHERE CartID = '" & strCartID & "'"
        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()

        Dim subtotal As Decimal
        Dim totalBeforeRound As Decimal
        Dim total As Decimal
        Dim tax As Decimal = 0.0975
        Dim totalTax As Decimal

        If dr.Read() Then
            If State.Text = "CA" Then
                subtotal = dr.Item("Subtotal")
                totalTax = subtotal * tax
                totalBeforeRound = totalTax + subtotal
                total = totalBeforeRound
            Else
                subtotal = dr.Item("Subtotal")
                total = subtotal
            End If
        End If
        conn.Close()



        strSQLStatement = "SELECT * FROM Customer WHERE OrderlineID = '" & strCartID & "'"
        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()

        Dim customerID As Integer

        If dr.Read() Then
            customerID = dr.Item("ID")
        End If
        conn.Close()

        strSQLStatement = "INSERT INTO OrderInfo (OrderlineID, Subtotal, Total, CustomerID) VALUES ('" & strCartID & "', " & subtotal & ", " & total & ", " & customerID & ")"
        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()
        conn.Close()

        '***begin authroize.net
        ' test server
        Dim post_url As String
        post_url = "https://test.authorize.net/gateway/transact.dll"

        ' name/value pairs
        Dim post_values As New Dictionary(Of String, String)
        post_values.Add("x_login", "29RZygU2U5b8") ' Jason's login ID
        post_values.Add("x_tran_key", "9285fRbw2j6FSH3d") ' Jason's transaction key
        post_values.Add("x_delim_data", "TRUE")
        post_values.Add("x_delim_char", "|")
        post_values.Add("x_relay_response", "FALSE")
        post_values.Add("x_type", "AUTH_CAPTURE")
        post_values.Add("x_method", "CC")
        post_values.Add("x_card_num", CreditCardNumber.Text)
        post_values.Add("x_exp_date", CreditCardExpirationMonth.Text + "/" + CreditCardExpirationYear.Text)
        post_values.Add("x_amount", total)
        post_values.Add("x_description", "CIS 466 Test Transaction")
        post_values.Add("x_first_name", FirstName.Text)
        post_values.Add("x_last_name", LastName.Text)
        post_values.Add("x_address", StreetAddress.Text)
        post_values.Add("x_state", State.Text)
        post_values.Add("x_zip", Zip.Text)

        ' converts them to the proper format "x_login=username&x_tran_key=a1B2c3D4"
        Dim post_string As String = ""
        For Each field As KeyValuePair(Of String, String) In post_values
            post_string &= field.Key & "=" & HttpUtility.UrlEncode(field.Value) & "&"
        Next
        post_string = Left(post_string, Len(post_string) - 1)

        ' create an HttpWebRequest object to communicate with Authorize.net
        Dim objRequest As HttpWebRequest = CType(WebRequest.Create(post_url), HttpWebRequest)
        objRequest.Method = "POST"
        objRequest.ContentLength = post_string.Length
        objRequest.ContentType = "application/x-www-form-urlencoded"

        ' send the data in a stream
        Dim myWriter As StreamWriter = Nothing
        myWriter = New StreamWriter(objRequest.GetRequestStream())
        myWriter.Write(post_string)
        myWriter.Close()

        ' create an HttpWebRequest object to process the returned values in a stream and convert it into a string
        Dim objResponse As HttpWebResponse = CType(objRequest.GetResponse(), HttpWebResponse)
        Dim responseStream As New StreamReader(objResponse.GetResponseStream())
        Dim post_response As String = responseStream.ReadToEnd()
        responseStream.Close()

        ' break the response string into an array
        Dim response_array As Array = Split(post_response, post_values("x_delim_char"), -1)

        ' the results are output to the screen in the form of an html numbered list.
        ' Response.Write("<OL>")
        
        If response_array.GetValue(0) = "1" Then
            Response.Write("<script>alert(""This transaction has been approved."");</script>")
            Response.Redirect("Receipt.aspx")
        ElseIf response_array.GetValue(0) = "2" Then
            Response.Write("<script>alert(""This transaction has been declined."");</script>")
        ElseIf response_array.GetValue(0) = "3" Then
            Response.Write("<script>alert(""The credit card number is invalid."");</script>")
        ElseIf response_array.GetValue(0) = "4" Then
            Response.Write("<script>alert(""This transaction has been declined."");</script>")
        ElseIf response_array.GetValue(0) = "5" Then
            Response.Write("<script>alert(""A valid amount is required."");</script>")
        ElseIf response_array.GetValue(0) = "6" Then
            Response.Write("<script>alert(""The credit card number is invalid."");</script>")
        ElseIf response_array.GetValue(0) = "7" Then
            Response.Write("<script>alert(""The credit card expiration date is invalid."");</script>")
        ElseIf response_array.GetValue(0) = "8" Then
            Response.Write("<script>alert(""The credit card has expired."");</script>")
        ElseIf response_array.GetValue(0) = "9" Then
            Response.Write("<script>alert(""The ABA code is invalid."");</script>")
        ElseIf response_array.GetValue(0) = "10" Then
            Response.Write("<script>alert(""The account number is invalid."");</script>")
        End If


        'This loop runs through the array
        'For Each value In response_array
        '    If value <> Nothing Then
        '        Response.Write("<LI>" & value & "</LI>" & vbCrLf)
        '    End If
        'Next
        '***End authorize.net

        strSQLStatement = "UPDATE OrderInfo SET AuthCode = '" & response_array(4) & "' WHERE CustomerID = " & customerID & " AND OrderlineID = '" & strCartID & "'"
        cmdSQL = New SqlCommand(strSQLStatement, conn)
        conn.Open()
        dr = cmdSQL.ExecuteReader()
        conn.Close()

        Dim MyMailMessage As New MailMessage()
        MyMailMessage.IsBodyHtml = True
        MyMailMessage.From = New MailAddress("henrylu1013@gmail.com")

        Dim strEmail As String
        strEmail = Email.Text
        MyMailMessage.To.Add(strEmail)
        MyMailMessage.Subject = "Order Confirmation"
        MyMailMessage.Body = "<html>Confirmation Email for " & FirstName.Text & " <br/><br/> Confirmation ID: " & strCartID & " </html>"

        Dim SMTPServer As New SmtpClient("smtp.gmail.com")
        SMTPServer.Port = 587
        SMTPServer.Credentials = New System.Net.NetworkCredential("henrylu1013@gmail.com", "")
        SMTPServer.EnableSsl = True

        Try
            SMTPServer.Send(MyMailMessage)

        Catch ex As SmtpException

        End Try

        'Response.Redirect("Receipt.aspx")

    End Sub
End Class

