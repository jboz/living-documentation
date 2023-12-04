require 'erb'

def preprocess_feature feature
  if feature.key?('background')
    preprocess_scenario feature['background']
  end
  if feature.key?('scenarios')
    feature['scenarios'].each do |scenario|
      preprocess_scenario scenario
    end
  end
end

def preprocess_scenario scenario
  if scenario.key?('steps')
    preprocess_steplist scenario['steps']
  end
  if scenario.key?('examples')
    preprocess_table_comments scenario['examples']['rows']
  end
end

def preprocess_steplist steplist
  steplist.each do |step|
    if step.key?('rows')
      preprocess_table_comments step['rows']
    end
  end
end

def preprocess_table_comments rows
  if rows.length > 0 && rows.first.key?('comments') && rows.first['comments'].length > 0 && rows.first['comments'].first['value'].match(/^#cols=/)
    cols = rows.first['comments'].first['value'][1..-1]
    rows.first['comments'].java_send :remove, [Java::int], 0
    rows.first["cols"] = cols
  end
  rows.each do |row|
    if row.key?('comments') && row['comments'].length > 0 && row['comments'].first['value'].match(/^#cells=/)
      cells = row['comments'].first['value'][7..-1].split(/,/)
      row['cell-styles'] = cells
      row['comments'].java_send :remove, [Java::int], 0
    end
  end
end

# parse feature and make the result available to the template via binding as 'feature' hash.
feature = com.github.domgold.doctools.asciidoctor.gherkin.MapFormatter.parse(feature_file_content)

preprocess_feature(feature)

erb_template = ERB.new(template_content)
rendered_template_output = erb_template.result(binding())

rendered_template_output
