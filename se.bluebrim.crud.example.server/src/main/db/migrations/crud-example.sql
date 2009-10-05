--
-- Script that creates a database for Crud example
-- The script was autogenerated from ISAC production database
--

/****** Object:  Table [dbo].[User]    Script Date: 05/08/2008 11:14:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[User](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[UserName] [varchar](15) NULL,
	[Password] [varchar](30) NOT NULL,
	[Name] [varchar](40) NULL,
	[Email] [varchar](50) NULL,
	[Phone] [varchar](20) NULL,
	[Mobile] [varchar](20) NULL,
	[Role] [int] NOT NULL DEFAULT (0),
 CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH FILLFACTOR = 85 ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO