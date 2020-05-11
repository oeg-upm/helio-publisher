package semanticgateway.service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import semanticgateway.model.DynamicView;
import semanticgateway.repository.DynamicViewRepository;

@Service
public class DynamicViewService {

	
	@Autowired
	private DynamicViewRepository dynamicViewRepository;
	
	public Stream<DynamicView> findAll() {	
		return StreamSupport.stream(dynamicViewRepository.findAll().spliterator(), false);
	}
	
	public void save(DynamicView view) {
		dynamicViewRepository.save(view);
	}
	
	public void remove(DynamicView view) {
		dynamicViewRepository.delete(view);
	}
	public void removeById(String viewId) {
		dynamicViewRepository.deleteById(viewId);
	}
	
	public DynamicView matchResource(String resource) {
		DynamicView view = null;
		Optional<DynamicView> optionalView = findAll().filter(dynamicView -> matchRoute( resource, dynamicView)).findFirst();
		if(optionalView.isPresent()) {
			view = optionalView.get();
		}
		return view;
		
	}
	
	private Boolean matchRoute(String resource, DynamicView view) {
		Boolean matches = false;
		if(view.getIsRegex()) {
			Pattern pattern = Pattern.compile(view.getResource());
			Matcher matcher = pattern.matcher(resource);
			matches = matcher.matches();
		}else {
			matches = resource.equals(view.getResource());
		}
		
		return matches;
	}
	
}
