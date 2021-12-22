Imports System.Data
Imports System.Data.SqlClient
Partial Class ProductList

    Inherits System.Web.UI.Page
    Protected Sub Page_Load(sender As Object, e As EventArgs) Handles Me.Load
        If Request.QueryString("SubCategoryID") <> "" Then
            ' generate the productlist
            SqlDSProductList.SelectCommand = "Select * From Product Where CategoryID = " & CInt(Request.QueryString("SubCategoryID"))
            rpProductList.DataBind()
            'lblMainCategory.Text = Request.QueryString("CategoryName")
        ElseIf Request.QueryString("ProductID") <> "" Then
            pnlProductList.Visible = False
            pnlProductDetail.Visible = True
        End If

    End Sub
End Class
