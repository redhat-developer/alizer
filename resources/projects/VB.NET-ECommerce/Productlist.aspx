<%@ Page Title="" Language="VB" MasterPageFile="~/MasterPage.master" AutoEventWireup="false" CodeFile="Productlist.aspx.vb" Inherits="ProductList" %>

<asp:Content ID="Content1" ContentPlaceHolderID="head" runat="Server">
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="ContentPlaceHolder1" runat="Server">
   

        <!-- ================ -->
        <!-- Products section -->
        <!-- ================ -->
                    <div>
                        <section>
                            <ul>
                                <asp:SqlDataSource ID="SqlDSSubCategory" runat="server" ConnectionString="<%$ ConnectionStrings:CategoryConnection %>" SelectCommand=""></asp:SqlDataSource>
                                <asp:Repeater ID="rpSubCategory" runat="server" DataSourceID="SqlDSSubCategory">
                                    <ItemTemplate>
                                        <div class="collection">
                                            <a href="ProductList.aspx?SubCategoryID=<%# Container.DataItem("CategoryID")%>&SubCategoryName=<%# Trim(Container.DataItem("CategoryName"))%>&MainCategoryName=<%# Container.DataItem("ParentName")%>"><%# Container.DataItem("CategoryName")%></a>
                                        </div>
                                    </ItemTemplate>
                                </asp:Repeater>
                            </ul>
                        </section>
                    </div>
                    <!--/category-products-->
           


    <div class="span9">
        <div class="collection">
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

                            <div class="image">
                                <a href="category.aspx?ProductID=<%# Eval("ProductID")%>">
                                    <img src="img/product/<%# Eval("ProductID")%>.jpg" width="300" height="225" alt="" /></a>
                            </div>

                            <a href="category.aspx?ProductID=<%# Eval("ProductID")%>">
                            <%# Eval("ProductName")%> </a>

                            <div class="price"><%# Eval("UnitPrice", "{0:c}")%></div>

                            <div class="clear"></div>
                            <br />

                        </ItemTemplate>
                    </asp:Repeater>
                </asp:Panel>
                <asp:Panel ID="pnlProductDetail" runat="server" Visible="False">
                </asp:Panel>
            </div>

        </div>
    </div>

    </div>

</asp:Content>

