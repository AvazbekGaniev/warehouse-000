package warehouseapp.warehouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import warehouseapp.warehouse.entity.*;
import warehouseapp.warehouse.payload.ApiResponse;
import warehouseapp.warehouse.payload.OutputDTO;
import warehouseapp.warehouse.payload.OutputProductDTO;
import warehouseapp.warehouse.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OutputService {
    @Autowired
    OutputProductRepository outputProductRepository;
    @Autowired
    OutputRepository outputRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    CurrencyRepository currencyRepository;

    public ApiResponse save(OutputDTO outputDTO) {
        Output output = new Output();
        output.setDate(outputDTO.getDate());
        String facture = "F " + UUID.randomUUID().toString().substring(0, 4);
        output.setFactureNumber(facture);
        Optional<Warehouse> optionalWarehouse = warehouseRepository.findById(outputDTO.getWarehouseId());
        if (!optionalWarehouse.isPresent()) return new ApiResponse("Not found", false);
        Warehouse warehouse = optionalWarehouse.get();
        output.setWarehouse(warehouse);

        Optional<Client> clientOptional = clientRepository.findById(outputDTO.getClientId());
        if (!clientOptional.isPresent()) return new ApiResponse("Not found", false);
        Client client = clientOptional.get();
        output.setClient(client);

        Optional<Currency> optionalCurrency = currencyRepository.findById(outputDTO.getCurrencyId());
        if (!optionalCurrency.isPresent()) return new ApiResponse("Not found", false);
        Currency currency = optionalCurrency.get();
        output.setCurrency(currency);

        outputRepository.save(output);

        List<OutputProductDTO> outputProductDTOList = outputDTO.getOutputProductDTOList();
        for (OutputProductDTO outputProductDTO : outputProductDTOList) {
            OutputProduct outputProduct = new OutputProduct();
            outputProduct.setAmount(outputProductDTO.getAmount());
            outputProduct.setPrice(outputProductDTO.getPrice());
            Optional<Product> optionalProduct = productRepository.findById(outputProductDTO.getProductId());
            if (!optionalProduct.isPresent()) return new ApiResponse("Not found", false);
            Product product = optionalProduct.get();
            outputProduct.setProduct(product);

            outputProduct.setOutput(output);
            outputProductRepository.save(outputProduct);
        }
        return new ApiResponse("saved", true);
    }
}
