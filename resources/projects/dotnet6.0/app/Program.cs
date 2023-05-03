using RazorPagesContacts;

var builder = Microsoft.AspNetCore.WebHost.CreateDefaultBuilder(args)
                .UseStartup<Startup>();

var app = builder.Build();

app.Run();