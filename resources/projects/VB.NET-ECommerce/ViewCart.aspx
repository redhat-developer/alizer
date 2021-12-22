<%@ Page Title="" Language="VB" MasterPageFile="~/MasterPage.master" AutoEventWireup="false" CodeFile="ViewCart.aspx.vb" Inherits="ViewCart" %>

<asp:Content ID="Content1" ContentPlaceHolderID="head" Runat="Server">
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="ContentPlaceHolder1" Runat="Server">
    <form id="form1" runat="server">

        <asp:Label ID="messages" runat="server"></asp:Label>
    <section class="order">

<asp:SqlDataSource ID="ViewCart" runat="server" ConnectionString="<%$ ConnectionStrings:CategoryConnection %>" SelectCommand="SELECT [ProductName], [UnitPrice], [Quantity] FROM [Cart]"></asp:SqlDataSource>
				
      
			<div class="row-fluid">
            <asp:GridView ID="GridView1" CssClass="table table-striped" GridLines="None" runat="server" AutoGenerateColumns="False" DataKeyNames="ID" DataSourceID="ViewCart" OnRowCommand="gvCartLine_RowCommand" ShowHeaderWhenEmpty="True">
                <EmptyDataTemplate>Your cart is empty.</EmptyDataTemplate>
                <Columns>
                    <asp:BoundField DataField="ProductID" HeaderText="PID" SortExpression="ProductID">
                        <ItemStyle Width="20%"></ItemStyle>
                    </asp:BoundField>
                    <asp:BoundField DataField="ProductName" HeaderText="Name" SortExpression="ProductName">
                        <ItemStyle Width="20%"></ItemStyle>
                    </asp:BoundField>
                    <asp:BoundField DataField="UnitPrice" HeaderText="Price" SortExpression="UnitPrice">
                        <ItemStyle Width="20%"></ItemStyle>
                    </asp:BoundField>
                    <asp:TemplateField HeaderText="Quantity" HeaderStyle-VerticalAlign="Middle" ItemStyle-HorizontalAlign="left">
                        <ItemTemplate>
                            <asp:TextBox ID="tbNewQuantity" runat="server" Width="100%" CssClass="form-control inline" Font-Size="Medium" Text='<%# Eval("Quantity") %>'></asp:TextBox>
                        </ItemTemplate>
                        <ItemStyle Width="20%"></ItemStyle>
                    </asp:TemplateField>
                    <asp:TemplateField HeaderText="" HeaderStyle-VerticalAlign="Middle" ItemStyle-HorizontalAlign="left">
                        <ItemTemplate>
                            <asp:Button ID="btnUpdate" runat="server" CssClass="btn btn-danger" CommandName="rowUpdate" Text="Update"></asp:Button>
                            <asp:Button ID="btnRemove" runat="server" CssClass="btn btn-danger" CommandName="rowRemove" Text="Remove"></asp:Button>
                        <ItemStyle Width="20%"></ItemStyle>
                        </ItemTemplate>
                    </asp:TemplateField>
                </Columns>
            </asp:GridView>
        <asp:Button ID="btnEmpty" runat="server" CssClass="btn btn-danger" CommandName="rowRemove" Text="Empty"></asp:Button>
        <div class="row-fluid">
            <div class="col-md-6">
                &nbsp;
            </div>
            <div class="col-md-6">
                <div class="pull-right">
                    <label>Subtotal:</label>
                    <asp:Label ID="subtotal" runat="server" Text="Label"></asp:Label>
                </div>
            </div>
        </div>
        <br />
        <br />
        <br />
        <br />
        <br />
        <br />
            <a href="Category.aspx" class="btn theme"></i>Continue Shopping</a>
            <a href="Checkout.aspx" class="btn theme">Checkout</a>
		 

			</section>
    </form>
    <asp:SqlDataSource ID="SqlDataSource1" runat="server" ConnectionString="<%$ ConnectionStrings:CategoryConnection %>" ProviderName="<%$ ConnectionStrings:CategoryConnection.ProviderName %>"></asp:SqlDataSource>

</asp:Content>

