Atividade Final: APIs de Desenvolvimento para Dispositivos Móveis
Esta atividade tem como propósito consolidar os conhecimentos adquiridos nas últimas três aulas, proporcionando a prática de integração com Firebase e APIs REST.

Tarefas:
1. Tela de Login:
   - Crie uma tela de login utilizando um provedor Smartphone.
   - Para testes, utilize o seguinte número de celular: +5511912345678
   - O código de verificação a ser utilizado será: 123456.

2. Logout:
   - Implemente uma opção de logout no aplicativo, permitindo que os usuários encerrem suas sessões de forma simples.

3. Integração com API REST:
   - Integre a API REST disponível no GitHub no seu aplicativo, especificamente a rota /car.
   - O JSON necessário para salvar e exibir no aplicativo pode ser encontrado no READ ME do aplicativo ou no READ ME do projeto Node Express
   - O campo imageUrl deve apontar para uma imagem armazenada no Firebase Storage.

4. Google Maps:
   - Integre o Google Maps para enviar a localização do place.

Recursos:
- Links do GitHub:
  - FTPR-Car-Android - Projeto inicial a ser entregue no GitHub.
  - FTPR-Car-Api-Node-Express - Servidor Node Express com a API REST a ser utilizada.

Ambos os projetos estão disponíveis no GitHub, e você pode realizar um fork para iniciar o seu trabalho.

Não é necessário publicar um novo servidor Node Express, pois seu aplicativo final deve ser capaz de se integrar com a API disponibilizada.

SEGUE JSON DA CARGA INICIAL DE DADOS:

[
  {
    "id": "001",
    "imageUrl": "https://www.encontracarros.com.br/wp-content/uploads/2021/01/honda-civic-sport-2021.jpg",
    "year": "2020/2021",
    "name": "Honda Civic",
    "licence": "ABC-1234",
    "place": {
      "lat": -23.5505,
      "long": -46.6333
    }
  },
  {
    "id": "002",
    "imageUrl": "https://media.toyota.com.br/57c54e86-8a8e-4262-9543-2ebb634ec654.png",
    "year": "2019/2020",
    "name": "Toyota Corolla",
    "licence": "DEF-5678",
    "place": {
      "lat": -22.9068,
      "long": -43.1729
    }
  },
  {
    "id": "003",
    "imageUrl": "https://miamiimports.com.br/wp-content/uploads/2023/12/Ford-Mustang-Shelby-Gt-500-09.jpg",
    "year": "2018/2018",
    "name": "Ford Mustang Shelby GT500",
    "licence": "GHI-9012",
    "place": {
      "lat": -19.9167,
      "long": -43.9345
    }
  },
  {
    "id": "004",
    "imageUrl": "https://go-racing.pl/blog/wp-content/uploads/2024/04/D72_0449s.jpg",
    "year": "2021/2021",
    "name": "Chevrolet Camaro",
    "licence": "JKL-3456",
    "place": {
      "lat": -15.7801,
      "long": -47.9292
    }
  },
  {
    "id": "005",
    "imageUrl": "https://uploads.vrum.com.br/2023/08/00ce520e-vw-golf-gti-2.0-tsi-modelo-2028-branco-de-frente-em-movimento-no-asfalto.jpg",
    "year": "2020/2020",
    "name": "Volkswagen Golf GTI",
    "licence": "MNO-7890",
    "place": {
      "lat": -12.9714,
      "long": -38.5014
    }
  },
  {
    "id": "006",
    "imageUrl": "https://www.pastorecc.com.br/site/photos/cars/1699/bg_iVaOuYvpb8BS7geQxgoI.jpeg",
    "year": "2017/2018",
    "name": "BMW 3 Series",
    "licence": "PQR-2345",
    "place": {
      "lat": -8.0476,
      "long": -34.877
    }
  },
  {
    "id": "007",
    "imageUrl": "https://i.ytimg.com/vi/gTX8efZS3qQ/maxresdefault.jpg",
    "year": "2019/2020",
    "name": "Audi A4",
    "licence": "STU-6789",
    "place": {
      "lat": -1.4558,
      "long": -48.4902
    }
  },
  {
    "id": "008",
    "imageUrl": "https://www.encontracarros.com.br/upload/mercedes/mercedes-classe-c-coupe-2020-05.jpg",
    "year": "2022/2023",
    "name": "Mercedes-Benz C-Class",
    "licence": "VWX-1234",
    "place": {
      "lat": -3.119,
      "long": -60.0217
    }
  },
  {
    "id": "009",
    "imageUrl": "https://i0.statig.com.br/bancodeimagens/9r/pz/j8/9rpzj8fqirunat81zar8i3hmd.jpg",
    "year": "2020/2021",
    "name": "Chevrolet Trailblazer",
    "licence": "YZA-5678",
    "place": {
      "lat": -25.4284,
      "long": -49.2733
    }
  },
  {
    "id": "010",
    "imageUrl": "https://revistacarro.com.br/wp-content/uploads/2018/05/preto_tcm305-162598.jpg",
    "year": "2021/2022",
    "name": "Toyota RAV4",
    "licence": "BCD-9012",
    "place": {
      "lat": -27.5954,
      "long": -48.548
    }
  },
    {
    "id": "-1059664715",
    "imageUrl": "https://firebasestorage.googleapis.com/v0/b/trabalhofinalapiscars.firebasestorage.app/o/images%2F8a77ad07-493b-4183-ba5b-df356dcb055b.jpg?alt=media&token=fe9a2124-62dc-4fc0-a35f-383b4632afa3",
    "licence": "ABC-1235",
    "name": "Nissan GTR",
    "place": {
      "lat": 37.4221941636812,
      "long": -122.08532653749
    },
    "year": "2008/2009"
  },
  {
    "id": "811367321",
    "imageUrl": "https://firebasestorage.googleapis.com/v0/b/trabalhofinalapiscars.firebasestorage.app/o/images%2F914c7193-8baa-4549-9a82-9e6f04f14d1c.jpg?alt=media&token=89d0fc0d-cc72-40eb-bf54-30a64acb2529",
    "licence": "FER-0A58",
    "name": "Ferrari 458 Pista",
    "place": {
      "lat": 37.4223584516933,
      "long": -122.085287645459
    },
    "year": "2015/2015"
  },
  {
    "id": "-582579121",
    "imageUrl": "https://firebasestorage.googleapis.com/v0/b/trabalhofinalapiscars.firebasestorage.app/o/images%2F3eec3f2c-c084-4f47-a6a5-b43919edeb52.jpg?alt=media&token=251ec5b5-47c9-422f-a16e-78efe951aeaf",
    "licence": "N/A",
    "name": "Corvette C6 GT3",
    "place": {
      "lat": -23.5178895170348,
      "long": -46.5474512055516
    },
    "year": "2006"
  },
  {
    "id": "892209503",
    "imageUrl": "https://firebasestorage.googleapis.com/v0/b/trabalhofinalapiscars.firebasestorage.app/o/images%2F009ce62f-bd19-4af6-a3a2-2636eaab6064.jpg?alt=media&token=468bdb82-92ba-4d39-936e-5c9357cb4a3d",
    "licence": "DEU CERTO, DEU CERTO!! ",
    "name": "Eu",
    "place": {
      "lat": -23.5541488689764,
      "long": -46.6347993537784
    },
    "year": "1989"
  }
]
