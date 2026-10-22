package com.proyecto.safebox.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.safebox.model.Mensajes;
import com.proyecto.safebox.model.Usuarios;
import com.proyecto.safebox.repository.MensajesRepository;
import com.proyecto.safebox.repository.UsuariosRepository;
import jakarta.persistence.EntityNotFoundException;

// Implementación concreta de MensajesService. Orquesta la interacción entre MensajesRepository, UsuariosRepository, CryptoService 
// y AuditoriaService para aplicar tanto la lógica de cifrado como el registro de auditoría de las operaciones.

@Service
@Transactional
public class MensajesServiceImpl extends MensajesService {

    private final MensajesRepository msgRepo;
    private final UsuariosRepository usRepo;
    private final CryptoService cryptoService;
    private final AuditoriaService auditoriaService;
 

    public MensajesServiceImpl(
            MensajesRepository repo,
            UsuariosRepository usuariosRepo,
            CryptoService cryptoService,
            AuditoriaService auditoriaService) {

        super(repo, usuariosRepo);
        this.msgRepo = repo;
        this.usRepo = usuariosRepo;
        this.cryptoService = cryptoService;
        this.auditoriaService = auditoriaService;
        
    }


    // LISTADOS


    @Override
    @Transactional(readOnly = true)
    public List<Mensajes> listar() {
        List<Mensajes> mensajes = msgRepo.findAll();
        mensajes.forEach(cryptoService::decryptMessage);
        return mensajes;
    }

    // ADMIN: lista SIN descifrar
    @Override
    @Transactional(readOnly = true)
    public List<Mensajes> listarSinDescifrar() {
        return msgRepo.findAll();
    }

    // USER: lista por usuario, DESCIFRANDO contenido
    @Override
    @Transactional(readOnly = true)
    public List<Mensajes> listarPorUsuario(Long usuarioId) {
        List<Mensajes> mensajes = msgRepo.findByUsuarioId(usuarioId);
        mensajes.forEach(cryptoService::decryptMessage); 
        return mensajes;
    }

    // ADMIN: lista por usuario SIN descifrar
    @Override
    @Transactional(readOnly = true)
    public List<Mensajes> listarPorUsuarioSinDescifrar(Long usuarioId) {
        return msgRepo.findByUsuarioId(usuarioId);
    }


    // OBTENER 


    @Override
    @Transactional(readOnly = true)
    public Optional<Mensajes> obtener(Long id) {
        return msgRepo.findById(id).map(m -> {
            cryptoService.decryptMessage(m);                       
            auditoriaService.registrarAccionItem("READ_ITEM", m.getId());
            return m;
        });
    }

    // CREAR / ACTUALIZAR / ELIMINAR


    @Override
    public Mensajes crear(Mensajes mensaje, Long usuarioId) {
        Usuarios u = usRepo.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        mensaje.setUsuario(u);

        // Cifrar antes de guardar
        cryptoService.encryptMessage(mensaje);

        Mensajes saved = msgRepo.save(mensaje);
        auditoriaService.registrarAccionItem("CREATE_ITEM", saved.getId());
        return saved;
    }

    @Override
    public Mensajes actualizar(Long id, Mensajes data) {
        Mensajes m = msgRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado"));

        m.setTitulo(data.getTitulo());
        m.setContenido(data.getContenido());     // nuevo contenido en texto plano

        // Volver a cifrar con el contenido actualizado
        cryptoService.encryptMessage(m);

        Mensajes saved = msgRepo.save(m);
        auditoriaService.registrarAccionItem("UPDATE_ITEM", saved.getId());
        return saved;
    }

    @Override
    public void eliminar(Long id) {
        if (!msgRepo.existsById(id)) {
            throw new EntityNotFoundException("Mensaje no encontrado");
        }
        msgRepo.deleteById(id);
        auditoriaService.registrarAccionItem("DELETE_ITEM", id);
    }
}
