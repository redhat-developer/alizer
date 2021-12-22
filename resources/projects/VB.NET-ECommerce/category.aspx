<%@ Page Title="" Language="VB" MasterPageFile="~/MasterPage.master" AutoEventWireup="false" CodeFile="Category.aspx.vb" Inherits="Default3" %>

<asp:Content ID="Content1" ContentPlaceHolderID="head" runat="Server">
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="ContentPlaceHolder1" runat="Server">
    
    <div class="container">
        <asp:Panel ID="ProductInfo" runat="server" Visible="true">
            <asp:SqlDataSource ID="SqlDSProduct" runat="server" ConnectionString="<%$ ConnectionStrings:CategoryConnection %>"
                SelectCommand=""></asp:SqlDataSource>

            <div class="item">
                 <br />
                 <br />

                        <asp:Repeater ID="ListView3" runat="server" DataSourceID="SqlDSProduct">
                            <ItemTemplate>

                                <div class="img">
                                    <img src="img/product/<%# Eval("ProductID")%>.jpg" width="300" height="225" alt="" /></div>

                                <div class="addtocart">
                                <div class="name"><%# Eval("ProductName")%></div>
                                <div class="desc"><%# Eval("ProductDescription")%></div>
                                <div class="price"><%# Eval("UnitPrice", "{0:c}")%></div>
                                <br />
                            </ItemTemplate>
                        </asp:Repeater>
                
                        <asp:Panel ID="Panel1" runat="server" Visible="false">
                            <form id="form2" runat="server">
                                <asp:TextBox ID="TextBox1" runat="server" ValidateRequestMode="Inherit" TextMode="Number" Text="1" Width="30"></asp:TextBox>
                                <br />
                                <br />
                                <asp:Button ID="Button1" runat="server" CssClass="button alt" Text="Add Cart" />
                                <br />
                                <button onclick="history.go(-1);">Go back</button>
                                <div class="clear"></div>
                            </form>
                        </asp:Panel>
                    </div>
            </div>
        </asp:Panel>
        <!-- ================ -->
        <!-- Products section -->
        <!-- ================ -->
        <section class="product">

            <div class="row">

                <div class="span3 hidden-phone">
                    <div class="sidebar">

                        <ul>
                            <asp:SqlDataSource ID="SqlDSSubCategory" runat="server" ConnectionString="<%$ ConnectionStrings:CategoryConnection %>" SelectCommand=""></asp:SqlDataSource>
                            <asp:Repeater ID="rpSubCategory" runat="server" DataSourceID="SqlDSSubCategory">
                                <ItemTemplate>
                                    <div class="panel-heading">
                                        <h4 class="panel-title"><a href="ProductList.aspx?SubCategoryID=<%# Container.DataItem("CategoryID")%>&SubCategoryName=<%# Trim(Container.DataItem("CategoryName"))%>&MainCategoryName=<%# Container.DataItem("ParentName")%>"><%# Container.DataItem("CategoryName")%></a></h4>
                                    </div>
                                </ItemTemplate>
                            </asp:Repeater>
                        </ul>
                    </div>
                    <!--/category-products-->

                </div>

                <div class="span9">
                    <div class="row-fluid">

                        <!-- Collection -->
                        <div class="tab-content sideline">
                            <asp:Label ID="lblMainCategory" runat="server"></asp:Label>

                            <asp:Label ID="lblSubCategory" runat="server"></asp:Label>
                            <br />
                            <br />

                            <asp:Panel ID="pnlProductList" runat="server">
                                <asp:SqlDataSource ID="SqlDSProductList" runat="server" ConnectionString="<%$ ConnectionStrings:CategoryConnection %>" SelectCommand=""></asp:SqlDataSource>
                                <asp:Repeater ID="rpProductList" runat="server" DataSourceID="SqlDSProductList">

                                    <ItemTemplate>

                                        <div class="image"><a href="Category.aspx?ProductID=<%# Eval("ProductID")%>">
                                            <img src="img/products/<%# Eval("ProductID")%>.jpg" width="300" height="225" alt="" /></a></div>
                                        <div class="name"><a><%# Eval("ProductName")%></a></div>
                                        <div class="price"><%# Eval("UnitPrice", "{0:c}")%></div>
                                        <div class="clear"></div>
                                        <br />

                                    </ItemTemplate>
                                </asp:Repeater>
                            </asp:Panel>
                        </div>
                        <!-- Collections end -->

                    </div>
                </div>

            </div>
        </section>

    </div>

</asp:Content>

